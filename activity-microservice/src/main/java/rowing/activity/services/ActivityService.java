package rowing.activity.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rowing.activity.domain.Activity;
import rowing.activity.domain.ActivityRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    /**
     * Create new ActivityService instance
     *
     * @param activityRepository the repository that will be used in the service
     */
    @Autowired
    public ActivityService(ActivityRepository activityRepository){
        this.activityRepository = activityRepository;
    }

    /**
     * Returns all activites in the storage
     * @return returns a list of all activities in the database
     */
    public List<Activity> activityList(){
        return new ArrayList<>(activityRepository.findAll());
    }
}
