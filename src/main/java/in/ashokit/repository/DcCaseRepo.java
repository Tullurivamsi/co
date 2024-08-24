package in.ashokit.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashokit.Entity.DcCaseEntity;

public interface DcCaseRepo  extends JpaRepository<DcCaseEntity,Serializable>{

	
	public DcCaseEntity findByAppId(Integer appId);
	
	
}

