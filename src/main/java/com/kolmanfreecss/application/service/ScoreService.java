package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.model.repository.ScoreRepository;
import com.sun.net.httpserver.HttpExchange;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class ScoreService {
    
    private ScoreRepository scoreRepository;
    
    public ScoreService() {
        scoreRepository = new ScoreRepository();
    }
    
    public void saveScore(long levelId, long score) {
        
        
        
        
        scoreRepository.addScore();
    }

    
    
}
