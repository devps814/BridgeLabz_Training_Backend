package com.greet.service;

import com.greet.model.Greeting;
import com.greet.repository.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetingServiceImpl implements GreetingService {
    private final GreetingRepository greetingRepository;

    @Autowired
    public GreetingServiceImpl(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    @Override
    public List<Greeting> getAllGreetings() {
        return greetingRepository.findAll();
    }

    @Override
    public Greeting getGreetingById(int id) {
        return greetingRepository.findById(id);
    }

    @Override
    public boolean createGreeting(Greeting greeting) {
        return greetingRepository.save(greeting);
    }

    @Override
    public boolean updateGreeting(Greeting greeting) {
        return greetingRepository.update(greeting);
    }

    @Override
    public boolean deleteGreeting(int id) {
        return greetingRepository.deleteById(id);
    }
}
