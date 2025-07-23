import { Component, EventEmitter, input, InputSignal, output, OutputEmitterRef } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import { DatePipe } from '@angular/common';
import { ChatService, UserService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat-list',
  imports: [DatePipe],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss'
})
export class ChatListComponent {

  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact: boolean = false;
  contacts: Array<UserResponse> = []
  chatSelected: OutputEmitterRef<ChatResponse> = output<ChatResponse>();

  constructor(
    private userService: UserService,
    private chatService: ChatService,
    private keyCloakService: KeycloakService
  ) {
   
  }

  searchContact() {
    this.userService.getAllUsers()
      .subscribe({
        next: (users: UserResponse[]) => {
          this.contacts = users;
          this.searchNewContact = true;
        }
      });
  }

  chatClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    }
    return lastMessage?.substring(0, 17) + '...';
  }

  selectContact(contact: UserResponse) {
    this.chatService.createChat({
      'sender-id': this.keyCloakService.userId as string,
      'receiver-id': contact.id as string
    }).subscribe({
      next: (res) => {
        const chat: ChatResponse = {
          id: res.response,
          name: contact.firstName + ' ' + contact.lastName,
          recipientOnline: contact.online,
          lastMessageTime: contact.lastSeen,
          senderId: this.keyCloakService.userId,
          receiverId: contact.id
        };
        this.chats().unshift(chat);
        this.searchNewContact = false;
        // Send new chat to the parent
        this.chatSelected.emit(chat);
      }
    })
  }
}
