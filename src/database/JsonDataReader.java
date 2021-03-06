package database;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dataTypes.Admin;
import dataTypes.DataBlob;
import dataTypes.Education;
import dataTypes.Employer;
import dataTypes.Employment;
import dataTypes.JobPosting;
import dataTypes.Professor;
import dataTypes.Review;
import dataTypes.Student;
import dataTypes.User;
import enums.JobPostingStatus;
import enums.JsonDataLabels;
import enums.Major;
import utilities.Logger;

/**
 * JsonDataReader class subclass of DataReader gets all the data from the JSON
 * files
 * 
 * @author Brennan Cain
 */
public class JsonDataReader extends DataReader {
    private DataBlob dataBlob;

    private String adminFilePath;
    private String studentFilePath;
    private String employerFilePath;
    private String professorFilePath;
    private String reviewFilePath;
    private String jobPostingFilePath;

    private static final JSONParser parser = new JSONParser();

    /**
     * Construtor method
     * 
     * @param adminFilePath      where the admins are stored
     * @param studentFilePath    where the students are stored
     * @param employerFilePath   where the employers are stored
     * @param professorFilePath  where the professors are stored
     * @param reviewFilePath     where the reviews are stored
     * @param jobPostingFilePath where the job postings are stored
     */
    public JsonDataReader(String adminFilePath, String studentFilePath, String employerFilePath,
            String professorFilePath, String reviewFilePath, String jobPostingFilePath) {
        this.adminFilePath = adminFilePath;
        this.studentFilePath = studentFilePath;
        this.employerFilePath = employerFilePath;
        this.professorFilePath = professorFilePath;
        this.reviewFilePath = reviewFilePath;
        this.jobPostingFilePath = jobPostingFilePath;
    }

    /**
     * Reads the data from the JSON files and stores it in a DataBlob object.
     */
    public DataBlob read() {
        Logger.getInstance().log("Reading from files");
        dataBlob = new DataBlob();

        ArrayList<Admin> adminList = readAdministrators();
        for (Admin admin : adminList) {
            dataBlob.addUser(admin);
        }

        ArrayList<Student> studentList = readStudents();
        for (Student student : studentList) {
            dataBlob.addUser(student);
        }

        ArrayList<Employer> employerList = readEmployers();
        for (Employer employer : employerList) {
            dataBlob.addUser(employer);
        }

        ArrayList<Professor> professorList = readProfessors();
        for (Professor professor : professorList) {
            dataBlob.addUser(professor);
        }

        ArrayList<Review> reviewList = readReviews();
        for (Review review : reviewList) {
            dataBlob.addReview(review);
        }

        ArrayList<JobPosting> jobPostingList = readJobPostings();
        for (JobPosting jobPosting : jobPostingList) {
            dataBlob.addJobPosting(jobPosting);
        }

        return dataBlob;
    }

    /**
     * Reads the administrators from the JSON file and returns them in an ArrayList.
     * 
     * @return ArrayList of administrators
     */
    private ArrayList<Admin> readAdministrators() {
        ArrayList<Admin> adminList = new ArrayList<Admin>();
        try {
            FileReader reader = new FileReader(adminFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            for (Object adminObj : jsonList) {
                JSONObject adminJson = (JSONObject) adminObj;
                Admin admin = new Admin.Builder().id(UUID.fromString((String) adminJson.get(JsonDataLabels.USER_ID)))
                        .username((String) adminJson.get(JsonDataLabels.USER_USERNAME))
                        .password((String) adminJson.get(JsonDataLabels.USER_PASSWORD))
                        .email((String) adminJson.get(JsonDataLabels.USER_EMAIL))
                        .firstName((String) adminJson.get(JsonDataLabels.USER_FIRSTNAME))
                        .lastName((String) adminJson.get(JsonDataLabels.USER_LASTNAME))
                        .approved((boolean) adminJson.get(JsonDataLabels.USER_APPROVED)).build();
                adminList.add(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminList;
    }

    /**
     * Reads the students from the JSON file and returns them in an ArrayList.
     * @return ArrayList of students
     */
    private ArrayList<Student> readStudents() {
        ArrayList<Student> studentList = new ArrayList<Student>();
        try {
            FileReader reader = new FileReader(studentFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            if (jsonList != null) {
                for (Object studentObj : jsonList) {
                    JSONObject studentJson = (JSONObject) studentObj;

                    ArrayList<String> skills = new ArrayList<String>();
                    JSONArray skillsJson = (JSONArray) studentJson.get(JsonDataLabels.STUDENT_SKILLS);
                    if (skillsJson != null) {
                        for (Object skillObj : skillsJson) {
                            skills.add((String) skillObj);
                        }
                    }

                    ArrayList<Employment> employments = new ArrayList<Employment>();
                    JSONArray employmentsJson = (JSONArray) studentJson.get(JsonDataLabels.STUDENT_EMPLOYMENTS);
                    if (employmentsJson != null) {
                        for (Object employmentObj : employmentsJson) {

                            JSONObject employmentJson = (JSONObject) employmentObj;

                            ArrayList<String> details = new ArrayList<String>();
                            JSONArray detailsJson = (JSONArray) employmentJson
                                    .get(JsonDataLabels.STUDENT_EMPLOYMENT_DETAILS);
                            if (detailsJson != null) {
                                for (Object detail : detailsJson) {
                                    details.add((String) detail);
                                }
                            }

                            Employment employment = new Employment.Builder()
                                    .company((String) employmentJson.get(JsonDataLabels.STUDENT_EMPLOYMENT_COMPANY))
                                    .title((String) employmentJson.get(JsonDataLabels.STUDENT_EMPLOYMENT_TITLE))
                                    .dates((String) employmentJson.get(JsonDataLabels.STUDENT_EMPLOYMENT_DATES))
                                    .details(details).build();
                            employments.add(employment);
                        }
                    }

                    ArrayList<Education> educations = new ArrayList<Education>();
                    JSONArray educationsJson = (JSONArray) studentJson.get(JsonDataLabels.STUDENT_EDUCATIONS);
                    if (educationsJson != null) {
                        for (Object educationObj : educationsJson) {
                            JSONObject educationJson = (JSONObject) educationObj;

                            Education education = new Education.Builder()
                                    .place((String) educationJson.get(JsonDataLabels.STUDENT_EDUCATION_PLACE))
                                    .gpa((double) educationJson.get(JsonDataLabels.STUDENT_EDUCATION_GPA))
                                    .gradDate((String) educationJson.get(JsonDataLabels.STUDENT_EDUCATION_GRADDATE))
                                    .build();
                            educations.add(education);
                        }
                    }

                    Major major = Major.NA;
                    String majorStr = (String) studentJson.get(JsonDataLabels.STUDENT_MAJOR);
                    if (majorStr.equalsIgnoreCase(Major.COMPUTER_SCIENCE.toString())) {
                        major = Major.COMPUTER_SCIENCE;
                    } else if (majorStr.equalsIgnoreCase(Major.COMPUTER_ENGINEERING.toString())) {
                        major = Major.COMPUTER_ENGINEERING;
                    } else if (majorStr.equalsIgnoreCase(Major.COMPUTER_INFORMATION_SYSTEMS.toString())) {
                        major = Major.COMPUTER_INFORMATION_SYSTEMS;
                    } else if (majorStr.equalsIgnoreCase(Major.INTEGRATED_INFORMATION_TECHNOLOGY.toString())) {
                        major = Major.INTEGRATED_INFORMATION_TECHNOLOGY;
                    }

                    Student student = new Student.Builder()
                            .id(UUID.fromString((String) studentJson.get(JsonDataLabels.USER_ID)))
                            .username((String) studentJson.get(JsonDataLabels.USER_USERNAME))
                            .password((String) studentJson.get(JsonDataLabels.USER_PASSWORD))
                            .email((String) studentJson.get(JsonDataLabels.USER_EMAIL))
                            .firstName((String) studentJson.get(JsonDataLabels.USER_FIRSTNAME))
                            .lastName((String) studentJson.get(JsonDataLabels.USER_LASTNAME))
                            .approved((boolean) studentJson.get(JsonDataLabels.USER_APPROVED)).major(major)
                            .createdResume((boolean) studentJson.get(JsonDataLabels.STUDENT_CREATEDRESUME))
                            .employments(employments).educations(educations).technicalSkills(skills)
                            .averageRating((double) studentJson.get(JsonDataLabels.STUDENT_AVERAGERATING))
                            .removed((boolean) studentJson.get(JsonDataLabels.REMOVED)).build();
                    studentList.add(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentList;
    }

    /**
     * Reads the employers from the JSON file and returns them in an ArrayList.
     * @return ArrayList of employers
     */
    private ArrayList<Employer> readEmployers() {
        ArrayList<Employer> employerList = new ArrayList<Employer>();
        try {
            FileReader reader = new FileReader(employerFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            if (jsonList != null) {
                for (Object employerObj : jsonList) {
                    JSONObject employerJson = (JSONObject) employerObj;

                    Employer employer = new Employer.Builder()
                            .id(UUID.fromString((String) employerJson.get(JsonDataLabels.USER_ID)))
                            .username((String) employerJson.get(JsonDataLabels.USER_USERNAME))
                            .password((String) employerJson.get(JsonDataLabels.USER_PASSWORD))
                            .email((String) employerJson.get(JsonDataLabels.USER_EMAIL))
                            .firstName((String) employerJson.get(JsonDataLabels.USER_FIRSTNAME))
                            .lastName((String) employerJson.get(JsonDataLabels.USER_LASTNAME))
                            .approved((boolean) employerJson.get(JsonDataLabels.USER_APPROVED))
                            .company((String) employerJson.get(JsonDataLabels.EMPLOYER_COMPANY))
                            .averageRating(((double) employerJson.get(JsonDataLabels.EMPLOYER_AVERAGERATING)))
                            .removed((boolean) employerJson.get(JsonDataLabels.REMOVED)).build();
                    employerList.add(employer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employerList;
    }

    /**
     * Reads the professors from the JSON file and returns them in an ArrayList.
     * @return ArrayList of professors
     */
    private ArrayList<Professor> readProfessors() {
        ArrayList<Professor> professorList = new ArrayList<Professor>();
        try {
            FileReader reader = new FileReader(professorFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            if (jsonList != null) {
                for (Object professorObj : jsonList) {
                    JSONObject professorJson = (JSONObject) professorObj;

                    Professor professor = new Professor.Builder()
                            .id(UUID.fromString((String) professorJson.get(JsonDataLabels.USER_ID)))
                            .username((String) professorJson.get(JsonDataLabels.USER_USERNAME))
                            .password((String) professorJson.get(JsonDataLabels.USER_PASSWORD))
                            .email((String) professorJson.get(JsonDataLabels.USER_EMAIL))
                            .firstName((String) professorJson.get(JsonDataLabels.USER_FIRSTNAME))
                            .lastName((String) professorJson.get(JsonDataLabels.USER_LASTNAME))
                            .approved((boolean) professorJson.get(JsonDataLabels.USER_APPROVED))
                            .removed((boolean) professorJson.get(JsonDataLabels.REMOVED)).build();
                    professorList.add(professor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return professorList;
    }

    /**
     * Reads the reviews from the JSON file and returns them in an ArrayList.
     * @return ArrayList of reviews
     */
    private ArrayList<Review> readReviews() {
        ArrayList<Review> reviewList = new ArrayList<Review>();
        try {
            FileReader reader = new FileReader(reviewFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            if (jsonList != null) {
                for (Object reviewObj : jsonList) {
                    JSONObject reviewJson = (JSONObject) reviewObj;

                    User reviewee = null;
                    for (User user : dataBlob.getUsers()) {
                        if (user.getId()
                                .equals(UUID.fromString((String) reviewJson.get(JsonDataLabels.REVIEW_REVIEWEE)))) {
                            reviewee = user;
                            break;
                        }
                    }
                    if (reviewee == null) {
                        throw new Exception("Student not found");
                    }

                    User reviewer = null;
                    for (User user : dataBlob.getUsers()) {
                        if (user.getId()
                                .equals(UUID.fromString((String) reviewJson.get(JsonDataLabels.REVIEW_REVIEWER)))) {
                            reviewer = user;
                            break;
                        }
                    }
                    if (reviewer == null) {
                        throw new Exception("Reviewer not found");
                    }

                    Review review = new Review.Builder()
                            .id(UUID.fromString((String) reviewJson.get(JsonDataLabels.REVIEW_ID))).reviewer(reviewer)
                            .reviewee(reviewee).rating((int) (long) reviewJson.get(JsonDataLabels.REVIEW_RATING))
                            .comment((String) reviewJson.get(JsonDataLabels.REVIEW_COMMENT))
                            .removed((boolean) reviewJson.get(JsonDataLabels.REMOVED)).build();
                    reviewList.add(review);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    /**
     * Reads the job postings from the JSON file and returns them in an ArrayList.
     * @return ArrayList of job postings
     */
    private ArrayList<JobPosting> readJobPostings() {
        ArrayList<JobPosting> jobPostingList = new ArrayList<JobPosting>();
        try {
            FileReader reader = new FileReader(jobPostingFilePath);
            JSONArray jsonList = (JSONArray) parser.parse(reader);
            if (jsonList != null) {
                for (Object jobPostingObj : jsonList) {
                    JSONObject jobPostingJson = (JSONObject) jobPostingObj;

                    Employer employer = null;
                    for (User e : dataBlob.getUsers()) {
                        if (e.getId().equals(
                                UUID.fromString((String) jobPostingJson.get(JsonDataLabels.JOBPOSTING_EMPLOYER)))) {
                            employer = (Employer) e;
                            break;
                        }
                    }
                    if (employer == null) {
                        throw new Exception("Employer not found");
                    }

                    ArrayList<String> requirements = new ArrayList<String>();
                    JSONArray requirementList = (JSONArray) jobPostingJson.get(JsonDataLabels.JOBPOSTING_REQUIREMENTS);
                    if (requirementList != null) {
                        for (Object requirementObj : requirementList) {
                            requirements.add((String) requirementObj);
                        }
                    }

                    JobPostingStatus status = JobPostingStatus.NA;
                    switch (((String) jobPostingJson.get(JsonDataLabels.JOBPOSTING_STATUS)).toLowerCase()) {
                    case "open":
                        status = JobPostingStatus.OPEN;
                        break;
                    case "closed":
                        status = JobPostingStatus.CLOSED;
                        break;
                    case "pending":
                        status = JobPostingStatus.PENDING;
                        break;
                    }

                    // Get students by getting UUIDS and then searching for each UUID
                    ArrayList<UUID> applicantIds = new ArrayList<UUID>();
                    JSONArray applicantList = (JSONArray) jobPostingJson.get(JsonDataLabels.JOBPOSTING_APPLICANTS);
                    if (applicantList != null) {

                        for (Object applicantObj : applicantList) {
                            applicantIds.add(UUID.fromString((String) applicantObj));
                        }
                    }

                    ArrayList<Student> applicants = new ArrayList<Student>();
                    for (UUID id : applicantIds) {
                        boolean userFound = false;
                        for (User u : dataBlob.getUsers()) {
                            if (u.getId().equals(id)) {
                                applicants.add((Student) u);
                                userFound = true;
                                break;
                            }
                        }

                        if (!userFound) {
                            throw new Exception("Student not found");
                        }
                    }

                    JobPosting jobPosting = new JobPosting.Builder()
                            .id(UUID.fromString((String) jobPostingJson.get(JsonDataLabels.JOBPOSTING_ID)))
                            .employer(employer).jobTitle((String) jobPostingJson.get(JsonDataLabels.JOBPOSTING_TITLE))
                            .description((String) jobPostingJson.get(JsonDataLabels.JOBPOSTING_DESCRIPTION))
                            .requirements(requirements)
                            .hourlyWage((double) jobPostingJson.get(JsonDataLabels.JOBPOSTING_HOURLYWAGE))
                            .status(status).applicants(applicants)
                            .removed((boolean) jobPostingJson.get(JsonDataLabels.REMOVED)).build();
                    jobPostingList.add(jobPosting);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobPostingList;
    }

}
