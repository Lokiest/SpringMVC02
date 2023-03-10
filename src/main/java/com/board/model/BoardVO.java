package com.board.model;

import java.sql.Date;

import lombok.Data;

@Data
public class BoardVO {
	
	private String mode; //글쓰기 답변글쓰기 글수정
	private int num;
	private String name;
	private String passwd;
	private String subject;
	private String content;
	private Date wdate;
	private int readnum;
	private String filename;
	private String originFilename;
	private long filesize;
	private int refer;
	private int lev;
	private int sunbun;
	private String old_filename;
	
}
