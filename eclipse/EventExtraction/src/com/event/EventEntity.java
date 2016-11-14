package com.event;

/**
 * 事件实体
 * @author chenbin
 *
 */
public class EventEntity {

	private String eid;
	private String sentence;
	private String participant;
	private String time;
	private String location;
	private String denoter;
	private String type;
	
	public EventEntity() {
		super();
	}


	public EventEntity(String eid, String sentence, String participant, String time, String location, String denoter,
			String type) {
		super();
		this.eid = eid;
		this.sentence = sentence;
		this.participant = participant;
		this.time = time;
		this.location = location;
		this.denoter = denoter;
		this.type = type;
	}



	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	
	public String getSentence() {
		return sentence;
	}


	public void setSentence(String sentence) {
		this.sentence = sentence;
	}


	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDenoter() {
		return denoter;
	}
	public void setDenoter(String denoter) {
		this.denoter = denoter;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
