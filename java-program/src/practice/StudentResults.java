package com.practice;

public class StudentResults {
	
	private int studentId;
	private int totalMarks;
	private double percentage;
	private String gradeClass;
	private String division;
	private String standard;
	
	public StudentResults(int studentId, int totalMarks, double percentage, String gradeClass, String division, String standard) {
		super();
		this.studentId = studentId;
		this.totalMarks = totalMarks;
		this.percentage = percentage;
		this.gradeClass = gradeClass;
		this.division = division;
		this.standard = standard;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public int getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getGradeClass() {
		return gradeClass;
	}

	public void setGradeClass(String gradeClass) {
		this.gradeClass = gradeClass;
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

	@Override
	public String toString() {
		return "StudentResults [studentId=" + studentId + ", totalMarks=" + totalMarks + ", percentage=" + percentage
				+ ", result=" + gradeClass + ", division=" + division + ", standard=" + standard + "]";
	}

	
}
