package com.voterverification.application.batch;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.voterverification.application.DTO.CSVToVoterDto;
import com.voterverification.application.DTO.VoterTableToCSV;
import com.voterverification.application.Entity.Voter;
import com.voterverification.application.repository.VoterRepository;

@Configuration
public class CSVToVoterBatchConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToVoterBatchConfig.class);

    @Autowired
    private VoterRepository voterRepository;
    
    @Bean
    public FlatFileItemReader<CSVToVoterDto> voterReader() {
        LOGGER.info("Initializing FlatFileItemReader for VoterCSV...");
        FlatFileItemReader<CSVToVoterDto> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new ClassPathResource("voters.csv"));
        fileItemReader.setName("CSV-READER");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setLineMapper(lineMapper());
        LOGGER.info("FlatFileItemReader for VoterCSV is set up.");
        return fileItemReader;
    }
    
    private LineMapper<CSVToVoterDto> lineMapper() {
        LOGGER.info("Setting up LineMapper for VoterCSV...");
        DefaultLineMapper<CSVToVoterDto> defaultLineMapper = new DefaultLineMapper<>();
        
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("serialNumber", "fullName", "fatherName", "voterAge", "houseNo", "street", "ward", "village");
        
        BeanWrapperFieldSetMapper<CSVToVoterDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(CSVToVoterDto.class);
        
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        LOGGER.info("LineMapper for VoterCSV is successfully set up.");
        return defaultLineMapper;
    }

    @Bean
    public CSVToVoterBatchConfig processor() {
        LOGGER.info("Initializing VoterProcessor...");
        return new CSVToVoterBatchConfig();
    }
    

    @Bean
    public RepositoryItemWriter<Voter> voterWriter() {
        LOGGER.info("Setting up RepositoryItemWriter for Voter entity...");
        RepositoryItemWriter<Voter> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(voterRepository);
        repositoryItemWriter.setMethodName("save");
        LOGGER.info("RepositoryItemWriter for Voter entity is successfully set up.");
        return repositoryItemWriter;
    }
    

    
    

    @Bean
    public Job importVoterJob(JobRepository jobRepository, Step processVoterStep) {
        LOGGER.info("Creating importVoterJob...");
        return new JobBuilder("importVoterJob", jobRepository)
                .start(processVoterStep)
                .build();
    }

    @Bean
    public Step processVoterStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<CSVToVoterDto> reader,
                                 ItemProcessor<CSVToVoterDto, Voter> processor,
                                 ItemWriter<Voter> writer) {
        LOGGER.info("Creating processVoterStep...");
        return new StepBuilder("processVoterStep", jobRepository)
                .<CSVToVoterDto, Voter>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
