import { Component, OnInit } from '@angular/core';
import { ChatListComponent } from "../../components/chat-list/chat-list.component";
import { ChatResponse, MessageResponse } from '../../services/models';
import { ChatService, MessageService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { subscribeOn } from 'rxjs';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-main',
  imports: [ChatListComponent, DatePipe],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit {
  
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: MessageResponse[] = [];
  
  constructor(
    private chatService: ChatService,
    private keycloakService: KeycloakService,
    private messageService: MessageService
  ) {
    
  }
  
  ngOnInit(): void {
    this.getAllChats();
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
    // this.selectedChat.unreadCount = 0;
  }

  setMessagesToSeen() {
    throw new Error('Method not implemented.');
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
}


