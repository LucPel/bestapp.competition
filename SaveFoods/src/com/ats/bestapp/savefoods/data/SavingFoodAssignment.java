package com.ats.bestapp.savefoods.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavingFoodAssignment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Comment> conversation;

	public SavingFoodAssignment(){
		conversation=new ArrayList<Comment>();
	}
	
	public List<Comment> getConversation() {
		return conversation;
	}

	public void setConversation(List<Comment> conversation) {
		this.conversation = conversation;
	}
	
	public void addComment(Comment comment){
		if(conversation==null){
			conversation=new ArrayList<Comment>();
		}
		this.conversation.add(comment);
	}
}
