package in.ashokit.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name="CO_TRIGGERS")
public class CoTriggerEntity {
	
	@Id
	private Integer trgId;
	
	private Long caseNum;
	
	@Lob
	private byte[] coPdf;
	
	private String trgStatus;

	public Integer getTrgId() {
		return trgId;
	}

	public void setTrgId(Integer trgId) {
		this.trgId = trgId;
	}

	public Long getCaseNum() {
		return caseNum;
	}

	public void setCaseNum(Long caseNum) {
		this.caseNum = caseNum;
	}

	public byte[] getCoPdf() {
		return coPdf;
	}

	public void setCoPdf(byte[] coPdf) {
		this.coPdf = coPdf;
	}

	public String getTrgStatus() {
		return trgStatus;
	}

	public void setTrgStatus(String trgStatus) {
		this.trgStatus = trgStatus;
	}
	
	
	
	
	

}
