
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import dataTypes.Education;
import dataTypes.Employment;
import dataTypes.JobPosting;
import dataTypes.Review;
import dataTypes.Student;
import dataTypes.User;
import database.Database;
import enums.Major;

/**
 * This class is the UI for the Student.
 */
public class StudentUI {
    private Student student;
    private Scanner scanner;

    /**
     * Constructor for StudentUI when logged in
     *
     * @param scanner Scanner object to read user input.
     * @param student The student to be used.
     */
    public StudentUI(Scanner scanner, Student student) {
        this.student = student;
        this.scanner = scanner;
    }

    /**
     * Constructor for StudentUI when not logged in.
     * Use when signing up
     * 
     * @param scanner Scanner object to read user input.
     */
    public StudentUI(Scanner scanner) {
        this.scanner = scanner;
        this.student = null;
    }

    /**
     * Runs the student main menu.
     */
    public void doMainMenu() {
        boolean keepLooping = true;
        int option = 0;
        System.out.println("Welcome Student " + student.getFirstName() + " " + student.getLastName());
        while (keepLooping) {
            System.out.println("Please select a valid option: ");
            System.out.println("1) View Job Listings");
            System.out.println("2) Apply to job");
            System.out.println("3) View Job Applications");
            System.out.println("4) View Resume");
            System.out.println("5) Edit Resume");
            System.out.println("6) View Reviews");
            System.out.println("7) Print Resume");
            System.out.println("0) Exit Neurotic Job Search");
            System.out.print("Enter your option: ");
            option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    doViewJobListings();
                    break;
                case 2:
                    doApplyToJob();
                    break;
                case 3:
                    doViewJobApplications();
                    break;
                case 4:
                    doViewResume();
                    break;
                case 5:
                    doEditResume();
                    break;
                case 6:
                    doViewReviews();
                    break;
                case 7:
                    doPrintResume();
                    break;
                case 0:
                    keepLooping = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }

        }
    }

    /**
     * Prints the student resume to a file.
     */
    private void doPrintResume() {
        System.out.println("Where would you like to save your resume: ");
        String fileName = scanner.nextLine();
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            fw.write(student.getPrintableResume().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Search and apply for a job.
     */
    private void doApplyToJob() {
        System.out.print("Type a keyword or leave empty to view all: ");
        String keyword = scanner.nextLine();
        ArrayList<JobPosting> jobPostings;
        if(!keyword.equals("")) {
            jobPostings = Database.getInstance().getOpenPostingByRequirement(keyword);

        } else {
            jobPostings = Database.getInstance().getOpenPostings();
        }
        if(jobPostings.size() == 0) {
            System.out.println("No job postings found");
            return;
        }
        System.out.println("Job Postings:");
        for(int i = 0; i <= jobPostings.size(); i++) {
            while(i < jobPostings.size() && jobPostings.get(i).getApplicants().contains(student)) {
                jobPostings.remove(i);
            }
            
            if(i < jobPostings.size())
                System.out.println(i + ") " + jobPostings.get(i));
        }
        System.out.print("Enter the number of the job you would like to apply to: ");
        int option = Integer.parseInt(scanner.nextLine());
        if(!keyword.equals("")) {
            jobPostings = Database.getInstance().getOpenPostingByRequirement(keyword);

        } else {
            jobPostings = Database.getInstance().getOpenPostings();
        }
        if(option < 0 || option >= jobPostings.size()) {
            System.out.println("Invalid option");
            return;
        }
        jobPostings.get(option).addApplicant(student);
        System.out.println("You have successfully applied to the " + jobPostings.get(option).getJobTitle() + " job at " + jobPostings.get(option).getEmployer().getCompany());

    }

    /**
     * View reviews of the student.
     */
    private void doViewReviews() {
        ArrayList<Review> reviews = Database.getInstance().getReviewsByReviewee(student);
        for (Review review : reviews) {
            System.out.println(review.toString());
        }
    }

    /**
     * View the resume of the student.
     */
    private void doViewResume() {
        System.out.println(student.getPrintableResume());
    }

    /**
     * View the job applications of the student.
     */
    private void doViewJobApplications() {
        ArrayList<JobPosting> jobPostings = Database.getInstance().getPostingsByStudent(student);
        for (JobPosting jobPosting : jobPostings) {
            System.out.println(jobPosting.toString());
        }
    }

    /**
     * Edit the student's resume.
     */
    private void doEditResume() {
        boolean keepEditing = true;
        while(keepEditing) {
            System.out.println("Please select an valid option:");
            System.out.println("1) Edit Major");
            System.out.println("2) Edit Educations");
            System.out.println("3) Edit Skills");
            System.out.println("4) Edit Employment");
            System.out.println("0) Stop Editing");
            System.out.print("Choice: ");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    doEditMajor();
                    break;
                case 2:
                    doEditEducations();
                    break;
                case 3:
                    doEditSkills();
                    break;
                case 4:
                    doEditEmployment();
                    break;
                case 0:
                    keepEditing = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        
        student.setCreated(true);
    }

    /**
     * Edit the student's employment history.
     */
    private void doEditEmployment() {
        ArrayList<Employment> employments = student.getEmployments();
        for (int i = 0; i < employments.size(); i++) {
            System.out.println(i+"\n"+employments.get(i));
        }
        boolean keepEditing = true;
        while(keepEditing) {
            System.out.println("Please select an option: ");
            System.out.println("1) Add Employment");
            System.out.println("2) Remove Employment");
            //TODO: Add an edit. In the meantime removed and add works
            System.out.println("0) Stop Editing\n");
            System.out.print("Choice: ");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    doAddEmployment();
                    break;
                case 2:
                    doRemoveEmployment();
                    break;
                case 0:
                    keepEditing = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        
    }

    /**
     * Remove an employment from the student's employment history.
     */
    private void doRemoveEmployment() {
        ArrayList<Employment> employments = student.getEmployments();
        for (int i = 0; i < employments.size(); i++) {
            System.out.println(i+"\n"+employments.get(i));
        }
        System.out.print("Please enter the index of the employment you would like to remove: ");
        int option = Integer.parseInt(scanner.nextLine());
        try {
            employments.remove(option);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid employment");
        }
        
    }

    /**
     * Add an employment to the student's employment history.
     */
    private void doAddEmployment() {
        System.out.print("Please enter the company name: ");
        String companyName = scanner.nextLine();
        System.out.print("Please enter the job title: ");
        String jobTitle = scanner.nextLine();
        System.out.print("Please enter the dates: ");
        String dates = scanner.nextLine();

        ArrayList<String> details = new ArrayList<String>();
        System.out.print("Please enter the descriptions (empty line to end): ");
        String detail = scanner.nextLine();
        while (!detail.isEmpty()) {
            details.add(detail);
            detail = scanner.nextLine();
        }
        student.addEmployment(new Employment.Builder().company(companyName).title(jobTitle).dates(dates).details(details).build());
        
    }

    /**
     * Edit the student's skills.
     */
    private void doEditSkills() {
        ArrayList<String> skills = student.getTechnicalSkills();
        for (int i = 0; i < skills.size(); i++) {
            System.out.println(i+": "+skills.get(i));
        }
        boolean keepEditing = true;
        while(keepEditing) {
            System.out.println("Please select an option:");
            System.out.println("1) Add Skill");
            System.out.println("2) Remove Skill");
            System.out.println("0) Stop Editing");
            System.out.print("Choice: ");
            switch (Integer.parseInt(scanner.nextLine())) {
                case 1:
                    doAddSkill();
                    break;
                case 2:
                    doRemoveSkill();
                    break;
                case 0:
                    keepEditing = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        
    }

    /**
     * Remove a skill from the student's skills.
     */
    private void doRemoveSkill() {
        ArrayList<String> skills = student.getTechnicalSkills();
        for (int i = 0; i < skills.size(); i++) {
            System.out.println(i+": "+skills.get(i));
        }
        System.out.print("Please enter the index of the skill you would like to remove: ");
        int option = Integer.parseInt(scanner.nextLine());
        try {
            skills.remove(option);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid skill");
        }
        
    }

    private void doAddSkill() {
        System.out.print("Please enter the skill: ");
        String skill = scanner.nextLine();
        student.addTechincalSkill(skill);
        
    }

    /**
     * Edit the student's educations.
     */
    private void doEditEducations() {
        ArrayList<Education> educations = student.getEducations();
        for (int i = 0; i < educations.size(); i++) {
            System.out.println(i+"\n"+educations.get(i));
        }
        boolean keepEditing = true;
        while(keepEditing) {
            System.out.println("Please select an option: ");
            System.out.println("1) Add Education");
            System.out.println("2) Remove Education");
            //TODO: Add an edit. In the meantime removed and add works
            System.out.println("0) Stop Editing");
            System.out.print("Choice: ");
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    doAddEducation();
                    break;
                case 2:
                    doRemoveEducation();
                    break;
                case 0:
                    keepEditing = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        
    }

    /**
     * Remove an education from the student's educations.
     */
    private void doRemoveEducation() {
        ArrayList<Education> educations = student.getEducations();
        for (int i = 0; i < educations.size(); i++) {
            System.out.println(i+"\n"+educations.get(i));
        }
        System.out.print("Please enter the index of the education you would like to remove: ");
        int option = Integer.parseInt(scanner.nextLine());
        try {
            educations.remove(option);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid education");
        }
        
    }

    /**
     * Add an education to the student's educations.
     */
    private void doAddEducation() {
        System.out.print("Please enter the name of the school: ");
        String schoolName = scanner.nextLine();
        System.out.print("Please enter your gpa: ");
        double gpa = Double.parseDouble(scanner.nextLine());
        System.out.print("Please enter the graduation date: ");
        String gradDate = scanner.nextLine();
        student.addEducation(new Education.Builder().place(schoolName).gpa(gpa).gradDate(gradDate).build());
        
    }

    /**
     * Edit the student's major.
     */
    private void doEditMajor() {
        boolean majorChanged = false;
        while (!majorChanged) {
            System.out.print("Please enter your major: ");
            String major = scanner.nextLine();
            if(major.equalsIgnoreCase(Major.COMPUTER_ENGINEERING.toString())) {
                student.setMajor(Major.COMPUTER_ENGINEERING);
                majorChanged = true;
            } else if(major.equalsIgnoreCase(Major.COMPUTER_SCIENCE.toString())) {
                student.setMajor(Major.COMPUTER_SCIENCE);
                majorChanged = true;
            } else if(major.equalsIgnoreCase(Major.INTEGRATED_INFORMATION_TECHNOLOGY.toString())) {
                student.setMajor(Major.INTEGRATED_INFORMATION_TECHNOLOGY);
                majorChanged = true;
            } else if(major.equalsIgnoreCase(Major.COMPUTER_INFORMATION_SYSTEMS.toString())) {
                student.setMajor(Major.COMPUTER_INFORMATION_SYSTEMS);
                majorChanged = true;
            } else {
                System.out.println("Invalid major");
            }
        }
        
    }

    /**
     * View all job listings.
     */
    private void doViewJobListings() {
        for (JobPosting posting : Database.getInstance().getOpenPostings()) {
            System.out.println(posting);
        }
    }

    /**
     * Signup a new student.
     */
    public void doSignup() {
        System.out.print("Email:");
        String email = scanner.nextLine();
        if(!User.isEmailValid(email)) {
            System.out.println("Invalid email");
            return;
        }
        if (Database.getInstance().getUserByEmail(email) != null) {
            System.out.println("Email already in use");
            return;
        }

        System.out.print("Username:");
        String username = scanner.nextLine();
        if (Database.getInstance().findByUsername(username) != null) {
            System.out.println("Username already exists");
            return;
        }

        System.out.print("Password:");
        String password = scanner.nextLine();
        System.out.print("First Name:");
        String firstName = scanner.nextLine();
        System.out.print("Last Name:");
        String lastName = scanner.nextLine();
        while (User.isEmailValid(email) == false) {
            System.out.print("Invalid email. Please enter a valid email: ");
            email = scanner.nextLine();
        }

        System.out.print("Would you like to create a resume now [yes/no]:");
        String doResume = scanner.nextLine();
        while (!doResume.equalsIgnoreCase("yes") && !doResume.equalsIgnoreCase("no")) {
            System.out.print("Invalid scanner. Would you like to create a resume now [yes/no]:");
            doResume = scanner.nextLine();
        }

        student = new Student.Builder().username(username)
                                                   .password(password)
                                                   .firstName(firstName)
                                                   .lastName(lastName)
                                                   .email(email)
                                                   .createdResume(false)
                                                   .build();
        if (doResume.equalsIgnoreCase("yes")) {
            doEditResume();
        }
        Database.getInstance().addUser(student);
    }
}
