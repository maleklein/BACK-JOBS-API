package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.service.JobService;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    JobService jobService;

    @InjectMocks
    JobController jobController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
    }

    @Test
    @DisplayName("GET /api/job/all retorna la lista de trabajos")
    void getAllJobs_success() throws Exception {
        Job job1 = new Job();
        job1.setId(1);
        job1.setName("Developer");
        job1.setSalary(5000);
        job1.setHours(2000);
        job1.setResources(3);

        Job job2 = new Job();
        job2.setId(2);
        job2.setName("Designer");
        job2.setSalary(4500);
        job2.setHours(1500);
        job2.setResources(1);

        List<Job> jobs = new ArrayList<>();
        jobs.add(job1);
        jobs.add(job2);

        when(jobService.getAllJobs()).thenReturn(jobs);

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Developer"))
                .andExpect(jsonPath("$[1].name").value("Designer"));
    }

    @Test
    @DisplayName("GET /api/job/all - Excepcion retornada por el service")
    void getAllJobs_exception() throws Exception {
        when(jobService.getAllJobs()).thenThrow(new RuntimeException("Service Error"));

        mockMvc.perform(get("/api/job/all"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Service Error"));
    }

    @Test
    @DisplayName("GET /api/job/{id} retorna el trabajo solicitado")
    void getJobById_success() throws Exception {
        Job job = new Job();
        job.setId(1);
        job.setName("Developer");
        job.setSalary(5000);
        job.setHours(2000);
        job.setResources(3);

        when(jobService.getJobById(1)).thenReturn(job);

        mockMvc.perform(get("/api/job/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Developer"));
    }

    @Test
    @DisplayName("GET /api/job/{id} - Excepcion retornada por el service")
    void getJobById_exception() throws Exception {
        when(jobService.getJobById(99)).thenThrow(new RuntimeException("Job no encontrado"));

        mockMvc.perform(get("/api/job/99"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Job no encontrado"));
    }
}
