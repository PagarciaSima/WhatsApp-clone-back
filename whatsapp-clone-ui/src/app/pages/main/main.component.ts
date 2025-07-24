import { Component, OnDestroy, OnInit } from '@angular/core';
import { PickerComponent } from "@ctrl/ngx-emoji-mart";
import { ChatListComponent } from "../../components/chat-list/chat-list.component";
import { ChatResponse, MessageRequest, MessageResponse } from '../../services/models';
import { ChatService, MessageService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Notification } from './notification';

@Component({
  selector: 'app-main',
  imports: [ChatListComponent, DatePipe, PickerComponent, FormsModule],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy {
  
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: MessageResponse[] = [];
  showEmojis:boolean = false;
  messageContent: string = '';
  socketClient: any = null;
  notificationSubscription: any;
  
  constructor(
    private chatService: ChatService,
    private keycloakService: KeycloakService,
    private messageService: MessageService
  ) {
    
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }
  
  ngOnInit(): void {
    this.getAllChats();
    this.initWebSocket();
  }
  
  private getAllChats() {
    this.chatService.getChatsByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      }
    });
  }
  
  logout() {
    this.keycloakService.logout();
  }
  
  userProfile() {
    this.keycloakService.accountManagement();
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      'chat-id': this.selectedChat.id as string
    }).subscribe({
      next: () => {

      }
    });
  }

  getAllChatMessages(chatId: string) {
    this.messageService.getMessages({
      'chat-id': chatId
    }).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
      }
    });
  }

  isSelfMessage(message: MessageResponse) {
    return message.senderId === this.keycloakService.userId;
  }

  uploadMediaFile(target: EventTarget|null) {
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  onClick() {
    this.setMessagesToSeen();
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT'
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          // Reset content and emoji
          this.messageContent = '';
          this.showEmojis = false;
        }
      });      
    }
  }

  getSenderId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.senderId as string;
    }
    return this.selectedChat.receiverId as string;
  }

  getReceiverId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.receiverId as string;
    }
    return this.selectedChat.senderId as string;
  }

  initWebSocket() {
    if (this.keycloakService.keyCloak.tokenParsed?.sub) {
      let ws = new SockJS('http://localhost:8080/ws')
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keycloakService.keyCloak.tokenParsed?.sub}/chat`;
      this.socketClient.connect({ 'Authorization': 'Bearer' + this.keycloakService.keyCloak.token },
        () => {
          this.notificationSubscription = this.socketClient.subscribe(subUrl, 
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);
              this.handleNotificaion(notification);
            },
            () => console.error('Error while connecting to websocket')

          )
        }
      )
    }
  }
  
  handleNotificaion(notification: Notification) {
    if (!notification) return;
    // Open chat
    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const message: MessageResponse = {
            senderId: notification.senderId,
            receiverId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString()
          };
          if (notification.type === 'IMAGE') {
            this.selectedChat.lastMessage = 'Attachment';
          } else {
            this.selectedChat.lastMessage = notification.content;
          }
          this.chatMessages.push(message);
          break;
        case 'SEEN':
          this.chatMessages.forEach(m => m.state = 'SEEN');
          break;
      }
    }
    // closed chat (notification from other chat)
    else {
      // Find the chat that this notification belongs to in the list of chats (even if it's not currently open)
      const destChat: ChatResponse | undefined = this.chats.find(c => c.id === notification.chatId);

      // If that chat exists and the notification type is NOT 'SEEN'
      if (destChat && notification.type !== 'SEEN') {
        // If the notification is a text message
        if (notification.type === 'MESSAGE') {
          destChat.lastMessage = notification.content; // Update the last message preview for that chat
        }
        // If the notification is an image
        else if (notification.type === 'IMAGE') {
          destChat.lastMessage = 'Attachment'; // Set last message preview as "Attachment"
        }
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadCount! += 1;
      }
      // No destination chat (create a new one)
      else if (notification.type === 'MESSAGE') {
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          receiverId: notification.receiverId,
          lastMessage: notification.content,
          name: notification.chatName,
          unreadCount: 1,
          lastMessageTime: new Date().toString()
        }
        this.chats.unshift(newChat);
      }
    }
  }
}


