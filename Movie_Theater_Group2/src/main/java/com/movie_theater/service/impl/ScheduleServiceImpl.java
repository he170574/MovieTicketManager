package com.movie_theater.service.impl;

import com.movie_theater.dto.ScheduleDTO;
import com.movie_theater.entity.CinemaRoom;
import com.movie_theater.entity.Schedule;
import com.movie_theater.repository.ScheduleRepository;
import com.movie_theater.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    private ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Optional<Schedule> getOne(Integer id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule parseDtoToEntity(ScheduleDTO scheduleDTO) {
        return Schedule.builder()
                .scheduleId(scheduleDTO.getScheduleId())
                .deleted(false)
                .scheduleTime(scheduleDTO.getScheduleTime())
                .build();
    }

    @Override
    public ScheduleDTO parseScheduleToDto(Schedule schedule) {
        return ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                .scheduleTime(schedule.getScheduleTime())
                .build();
    }

    @Override
    public Boolean delete(Integer id) {
       Optional<Schedule> optionalSchedule = getOne(id);
       if (optionalSchedule.isPresent()) {
           Schedule schedule = optionalSchedule.get();
           schedule.setDeleted(Boolean.TRUE);
           return true;
        }
       return false;
    }

    @Override
    public Optional<Schedule> getScheduleByTime(LocalDateTime localDateTime) {
        return scheduleRepository.getSchedulesByScheduleTime(localDateTime);
    }

    @Override
    public List<Schedule> getByCinemaRoom(CinemaRoom cinemaRoom) {
        return scheduleRepository.getByCinemaRoom(cinemaRoom);
    }

    @Override
    public List<Schedule> getScheduleByMovieId(int movieId) {
        return scheduleRepository.getScheduleByMovieId(movieId);
    }
}
