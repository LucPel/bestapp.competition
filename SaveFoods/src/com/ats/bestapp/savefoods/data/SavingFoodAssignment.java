package com.ats.bestapp.savefoods.data;

import java.util.List;

public class SavingFoodAssignment {

	private List<Comment> conversation;

	public List<Comment> getConversation() {
		return conversation;
	}

	public void setConversation(List<Comment> conversation) {
		this.conversation = conversation;
	}
	
	public void addComment(Comment comment){
		this.conversation.add(comment);
	}
}
