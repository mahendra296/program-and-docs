package com.practice;

public class StudentDetails {
	
	private int studentId;
	private String studentName;
	private String division;
	private String standard;
	
	public StudentDetails(int studentId, String studentName, String division, String standard) {
		super();
		this.studentId = studentId;
		this.studentName = studentName;
		this.division = division;
		this.standard = standard;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}
	
}
