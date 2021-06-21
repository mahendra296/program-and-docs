package com.practice;

import java.util.ArrayList;
import java.util.List;

public class GenerateStudentResult implements Result {
	public List<StudentResults> studentResult = new ArrayList<StudentResults>();
	//StudentMarks student1 = new StudentMarks(1, "Dhruvil", "Div-A", "standard-1", 41, 50, 35, 55, 45);
	//List<StudentMarks> students = new ArrayList<StudentMarks>();
	@Override
	public List<StudentResults> calculateResult(List<StudentMarks> l) {
		
		for(StudentMarks s : l)
		{
			String gradeClass = null;
			int total = s.getSubject1() + s.getSubject2() + s.getSubject3() + s.getSubject4() + s.getSubject5();
			
			double percentage = (total*100)/500;
			
			if(percentage > 70)
				gradeClass = "Distiction";
			else if(percentage > 60 && percentage <= 70)
				gradeClass = "First";
			else if(percentage > 50 && percentage <= 60)
				gradeClass = "Second";
			else if(percentage >= 35 && percentage <= 50)
				gradeClass = "pass";
			else
				gradeClass = "fail";
			
			if(s.getSubject1() < 35 || s.getSubject2() < 35 || s.getSubject3() < 35 || 
					s.getSubject4() < 35 || s.getSubject5() < 35) {
				gradeClass = "fail";
			}
			
			studentResult.add(new StudentResults(s.getStudentId(), total, percentage, gradeClass, s.getDivision(), s.getStandard()));
		}
		
		return studentResult;
	}

	

}
