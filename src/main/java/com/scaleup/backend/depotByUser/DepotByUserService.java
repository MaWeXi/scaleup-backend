package com.scaleup.backend.depotByUser;


import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.userByLeague.DTO.PortfolioAndDepotValue;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import com.scaleup.backend.userByLeague.UserByLeagueService;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Service
public class DepotByUserService {

    final DepotByUserRepository depotByUserRepository;
    final UserByLeagueService userByLeagueService;
    final UserByLeagueRepository userByLeagueRepository;

    public DepotByUserService(DepotByUserRepository depotByUserRepository, UserByLeagueService userByLeagueService, UserByLeagueRepository userByLeagueRepository) {
        this.depotByUserRepository = depotByUserRepository;
        this.userByLeagueService = userByLeagueService;
        this.userByLeagueRepository = userByLeagueRepository;
    }

    @Scheduled(cron="0 0 0 * * *")
    public void updateAllDepots() {
        try {
            List<UserByLeague> userByLeagueList = userByLeagueRepository.findAll();
            if (userByLeagueList.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "There are no users in this league");
            }
            String leagueId;
            String userId;
            LocalDate todayDate = LocalDate.now();
            LocalDateTime todayTime = todayDate.atStartOfDay();

            for (UserByLeague userByLeague : userByLeagueList) {
                leagueId = userByLeague.getLeagueId();
                userId = userByLeague.getUserId();
                PortfolioAndDepotValue portfolioAndDepotValue = userByLeagueService.getCurrentPortfolioAndDepotValue(leagueId, userId);
                DepotByUser depotByUser = new DepotByUser(leagueId, userId, todayTime, portfolioAndDepotValue.getPortfolioValue(), portfolioAndDepotValue.getDepotValue());
                depotByUserRepository.save(depotByUser);
            }
        }catch (Exception e){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
