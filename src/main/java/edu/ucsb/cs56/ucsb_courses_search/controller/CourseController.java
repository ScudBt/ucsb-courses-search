package edu.ucsb.cs56.ucsb_courses_search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ucsb.cs56.ucsb_courses_search.entity.ScheduleItem;
import edu.ucsb.cs56.ucsb_courses_search.entity.Schedule;
import edu.ucsb.cs56.ucsb_courses_search.repository.ScheduleItemRepository;
import edu.ucsb.cs56.ucsb_courses_search.repository.ScheduleRepository;
import edu.ucsb.cs56.ucsb_courses_search.service.QuarterListService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CourseController {

    private Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // @Autowired
    // private QuarterListService quarterListService;

    @Autowired
    public CourseController(ScheduleItemRepository scheduleItemRepository, ScheduleRepository scheduleRepository) {
        this.scheduleItemRepository = scheduleItemRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/courseschedule")
    public String index(Model model, OAuth2AuthenticationToken token) {
        
        logger.info("Inside /courseschedule controller method CourseController#index");
        logger.info("model=" + model + " token=" + token);

        if (token!=null) {
            String uid = token.getPrincipal().getAttributes().get("sub").toString();
            logger.info("uid="+uid);
            logger.info("scheduleItemRepository="+scheduleItemRepository);
            List<Schedule> myschedules = scheduleRepository.findByUid(uid);// get all schedule ids by uid
            // get courses by each scheduleid to a list
            Schedule lastSchedule = myschedules.get(myschedules.size() - 1);
            Iterable<ScheduleItem> myclasses = scheduleItemRepository.findByScheduleid(lastSchedule.getScheduleid());
            // logger.info("there are " + myclasses.size() + " courses that match uid: " + uid);
            model.addAttribute("myclasses", myclasses);
            model.addAttribute("myschedules", myschedules);
            //model.addAttribute("quarters", quarterListService.getQuarters());
        } else {
            ArrayList<ScheduleItem> emptyList = new ArrayList<ScheduleItem>();
            model.addAttribute("myclasses", emptyList);
        }
        return "courseschedule/index";
    }
    @PostMapping("/courseschedule/add/{scheduleid}")
    public String add(@PathVariable("scheduleid") long scheduleid, ScheduleItem scheduleItem, Model model) {
        scheduleItem.setScheduleid(scheduleid);
        scheduleItemRepository.save(scheduleItem);
        return "redirect:/courseschedule";
    }

   @PostMapping("/courseschedule/create")
    public String add_schedule(String sname, Model model, OAuth2AuthenticationToken token) {
        if (token!=null){
            Schedule newschedule = new Schedule();
            String uid = token.getPrincipal().getAttributes().get("sub").toString();
            newschedule.setUid(uid);
            newschedule.setSchedulename(sname);
            //newschedule.setQuarter(quarter);
            scheduleRepository.save(newschedule); 
        }
        return "redirect:/courseschedule";
    }

}
