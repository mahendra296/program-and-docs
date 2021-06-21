package com.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeclareResult {

	public static void main(String[] args) {
		List<String> divisions = Arrays.asList("Div-A", "Div-B", "Div-C", "Div-D");
		List<StudentResults> perDivision = new ArrayList<StudentResults>();
		List<StudentResults> perStandard = new ArrayList<StudentResults>();
		GenerateStudentResult report = new GenerateStudentResult();
		List<StudentMarks> students = new ArrayList<StudentMarks>();
		students.add(new StudentMarks(1, "Dhruvil", "Div-A", "standard-1", 41, 50, 35, 55, 45));
		students.add(new StudentMarks(2, "Jigar", "Div-A", "standard-1", 8, 50, 35, 55, 45));
		students.add(new StudentMarks(3, "Ramesh", "Div-A", "standard-1", 70, 66, 80, 85, 69));
		students.add(new StudentMarks(4, "Rahul", "Div-B", "standard-1", 41, 50, 35, 45, 37));
		students.add(new StudentMarks(5, "Jignesh", "Div-B", "standard-1", 8, 50, 35, 55, 45));
		students.add(new StudentMarks(6, "Nirmal", "Div-B", "standard-1", 85, 75, 89, 90, 95));
		students.add(new StudentMarks(7, "Manoj", "Div-A", "standard-2", 35, 30, 55, 50, 40));
		students.add(new StudentMarks(8, "Manan", "Div-A", "standard-2", 59, 75, 80, 85, 79));
		students.add(new StudentMarks(9, "Manoj", "Div-B", "standard-2", 35, 30, 55, 50, 40));
		students.add(new StudentMarks(10, "Manan", "Div-B", "standard-2", 75, 75, 80, 85, 79));

		List<StudentResults> results = report.calculateResult(students);

		Collections.sort(results, (i, j) -> i.getPercentage() < j.getPercentage() ? 1 : -1);

		System.out.println("Total Students Size : " + results.size());

		System.out.println("Output-1: All Students Results. \n=======================");

		results.stream().forEach(System.out::println);

		System.out.println("===============================================================");
		System.out.println("Output-2: Per division highest students in standard");

		Map<String, List<StudentResults>> resultMap = results.stream()
				.collect(Collectors.groupingBy(StudentResults::getStandard));

		resultMap.forEach((key, value) -> {
			divisions.stream().forEach(div -> {
				List<StudentResults> standardList = value.stream().filter(it -> it.getDivision().contains(div)).collect(Collectors.toList());
				Optional<StudentResults> studentResult = standardList.stream().max(Comparator.comparing(StudentResults::getPercentage));
				if (studentResult.isPresent()) {
					perDivision.add(studentResult.get());
				}
			});

		});

		perDivision.forEach(System.out::println);
		System.out.println("===============================================================");
		
		System.out.println("Output-3: per standard highest students");
		
		resultMap.forEach((key, value) -> {
			Optional<StudentResults> studentResult = value.stream().max(Comparator.comparing(StudentResults::getPercentage));
			if (studentResult.isPresent()) {
				perStandard.add(studentResult.get());
			}
		});
		
		perStandard.forEach(System.out::println);
		
	}

}
