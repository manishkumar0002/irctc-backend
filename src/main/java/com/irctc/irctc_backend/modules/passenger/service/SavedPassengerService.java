package com.irctc.irctc_backend.modules.passenger.service;

import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.modules.passenger.entity.SavedPassenger;
import com.irctc.irctc_backend.modules.passenger.repository.SavedPassengerRepository;
import com.irctc.irctc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedPassengerService {

    private final SavedPassengerRepository savedPassengerRepository;
    private final UserRepository userRepository;

    public List<SavedPassenger> getSavedPassengers(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return savedPassengerRepository.findByUser(user);
    }

    @Transactional
    public SavedPassenger savePassenger(String email, SavedPassenger input) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        input.setUser(user);
        return savedPassengerRepository.save(input);
    }

    @Transactional
    public SavedPassenger updatePassenger(Long passengerId, SavedPassenger input) {
        SavedPassenger db = savedPassengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Saved passenger not found"));
        db.setName(input.getName());
        db.setAge(input.getAge());
        db.setGender(input.getGender());
        db.setBerthPreference(input.getBerthPreference());
        return savedPassengerRepository.save(db);
    }

    @Transactional
    public void deletePassenger(Long passengerId) {
        savedPassengerRepository.deleteById(passengerId);
    }
}
