package in.ashokit.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashokit.Entity.DcIncomeEntity;

public interface DcIncomeRepository  extends JpaRepository<DcIncomeEntity,Serializable>{
	
	
	public DcIncomeEntity findByCaseNum(Long caseNum);

}
