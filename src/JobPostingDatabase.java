import java.util.ArrayList;

public class JobPostingDatabase {
    private static JobPostingDatabase jobPostingDatabase;
    private ArrayList<JobPosting> postings;
    private ArrayList<JobPosting> removedPostings;

    private JobPostingDatabase() {
        postings = new ArrayList<JobPosting>();
        removedPostings = new ArrayList<JobPosting>();
    }

    public static JobPostingDatabase getInstance() {
        if(jobPostingDatabase == null)
            jobPostingDatabase = new JobPostingDatabase();
        return jobPostingDatabase;
    }

    public void addPosting(JobPosting posting) {
        // add posting to database 
    }

  
}
