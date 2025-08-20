package com.voterverification.application.batch;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.voterverification.application.DTO.VoterTableToCSV;
import com.voterverification.application.Entity.Voter;
import com.voterverification.application.repository.VoterRepository;

@Configuration
public class VoterToCSVFileBatchConfig {
	
	@Autowired
	private VoterRepository voterRepository;
	
	@Bean
    public RepositoryItemReader<Voter> voterToCSVReader(VoterRepository voterRepository) {
        return new RepositoryItemReaderBuilder<Voter>()
                .name("voterReader")
                .repository(voterRepository)
                .methodName("findAll") // Uses Spring Data JPA to find all Voter records
                .arguments(Collections.emptyList()) // No arguments for findAll
                .sorts(Collections.singletonMap("serialNumber", Sort.Direction.ASC)) // Sorting by serialNumber
                .build();
    }
	
	@Bean(value = "voterToCSVProcessor")
	public VoterToCSVProcessor voterToCSVProcessor() {
		return new VoterToCSVProcessor();
	}
	
	
	
	
	
	@Bean
	public FlatFileItemWriter<VoterTableToCSV> voterToCSVWritter(){
		
		BeanWrapperFieldExtractor<VoterTableToCSV> filedExtractor = new BeanWrapperFieldExtractor<>();
		filedExtractor.setNames(new String[] {"serialNumber", "fullName","fatherName","voterAge","voted","verifiedTime","houseNo","street","ward","village","verifiedBy"});
		filedExtractor.afterPropertiesSet();
		
		
		DelimitedLineAggregator<VoterTableToCSV> delimitedLineAggregator= new DelimitedLineAggregator<>();
		delimitedLineAggregator.setDelimiter(",");
		delimitedLineAggregator.setFieldExtractor(filedExtractor);
		
		return new FlatFileItemWriterBuilder<VoterTableToCSV>().name("VoterTableToCSV").resource(new FileSystemResource("output/voters_list_output.csv")).lineAggregator(delimitedLineAggregator)
				.headerCallback(new FlatFileHeaderCallback() {
			        @Override
			        public void writeHeader(Writer writer) throws IOException {
			            // Define your CSV header here
			            writer.write("serialNumber,fullName,fatherName,voterAge,voted,verifiedTime,houseNo,street,ward,village,verifiedBy");
			        }
			    })
				.build();
	}
	


	    @Bean
	    public Job exportVoterToCSVJob(JobRepository jobRepository,@Qualifier(value = "exportVoterToCSVStep") Step exportVoterStep ) {
	        return new JobBuilder("exportVoterToCSVJob", jobRepository)
	                .start(exportVoterStep)
	                .build();
	    }

	    @Bean
	    public Step exportVoterToCSVStep(JobRepository jobRepository,
	                                PlatformTransactionManager transactionManager,
	                               @Qualifier("voterToCSVReader") ItemReader<Voter> reader,
	                               @Qualifier("voterToCSVProcessor") ItemProcessor<Voter, VoterTableToCSV> processor,
	                               @Qualifier("voterToCSVWritter") ItemWriter<VoterTableToCSV> writer) {

	        return new StepBuilder("exportVoterToCSVStep", jobRepository)
	                .<Voter, VoterTableToCSV>chunk(100, transactionManager)
	                .reader(reader)
	                .processor(processor)
	                .writer(writer)
	                .build();
	    }


}
