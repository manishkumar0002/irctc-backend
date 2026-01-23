package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.dto.TrainRequest;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.repository.TrainRepository;
import com.irctc.irctc_backend.repository.TrainRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;

    //  Admin adds train → route auto-created
    @Transactional
    public Train addTrain(TrainRequest request) {

        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber());
        train.setTrainName(request.getTrainName());
        train.setTotalSeats(request.getTotalSeats());

        Train savedTrain = trainRepository.save(train);

        //  CRITICAL FIX
        TrainRoute route = new TrainRoute();
        route.setTrain(savedTrain);
        trainRouteRepository.save(route);

        return savedTrain;
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }
}
