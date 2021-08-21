package com.example.chatapp.Chat;

public class MessageObject {
  String messageId,
         senderId,
           message;

  public MessageObject(String messageId,String senderId,String message){
      this.messageId=messageId;
      this.senderId=senderId;
      this.message=message;
  }
  public String getMessageId()
    {return messageId;}
  public String getsenderId()
    {return senderId;}
    public String getMessage()
      {return message;}
}
