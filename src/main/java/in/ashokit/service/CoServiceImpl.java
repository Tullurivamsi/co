package in.ashokit.service;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.ashokit.Entity.CitizenAppEntity;
import in.ashokit.Entity.CoTriggerEntity;
import in.ashokit.Entity.DcCaseEntity;
import in.ashokit.Entity.EligDtlsEntity;
import in.ashokit.binding.CoResponse;
import in.ashokit.repository.CitizenAppRepository;
import in.ashokit.repository.CoTriggerRepository;
import in.ashokit.repository.DcCaseRepo;
import in.ashokit.repository.EligDtlsRepository;
import in.ashokit.utils.EmailUtils;

@Service
public class CoServiceImpl implements CoService {

	@Autowired
	private CoTriggerRepository coTrgRepo;

	@Autowired
	private EligDtlsRepository eligRepo;

	@Autowired
	private CitizenAppRepository appRepo;

	@Autowired
	private DcCaseRepo dcCaseRepo;

	@Autowired
	private EmailUtils emailUtils;
	
	

	@Override
	public CoResponse processPendingTriggers() {
		
		CoResponse response =new CoResponse();
		
		
		CitizenAppEntity appEntity=null;
		
		List<CoTriggerEntity> pendingTrgs =coTrgRepo.findByTrgStatus("pending");
		
		response.setTotalTriggers(Long.valueOf(pendingTrgs .size()));
		
		for(CoTriggerEntity entity : pendingTrgs)
		{
			Long caseNum = entity.getCaseNum();
			
			EligDtlsEntity elig = eligRepo.findByCaseNum(entity.getCaseNum());
			
			Optional<DcCaseEntity>  k = dcCaseRepo.findById(entity.getCaseNum());
			
			if(k.isPresent())
			{
				DcCaseEntity dcCaseEntity =k.get();
				
				Integer appId=dcCaseEntity.getAppId();
				
				 Optional<CitizenAppEntity> appEntityOptional = appRepo.findById(appId);
				 
				 if(appEntityOptional.isPresent())
				 {
					appEntity =appEntityOptional.get();
					 
					
				 }
			}
			
			Long failed =0l;
	        Long  success =0l;
	        
			try {
				generateAndSendPdf(elig,appEntity);
				success++;
				

			}catch(Exception e)
			{
				e.printStackTrace();
				failed++;
			}
			
			response.setSuccTriggers(success);
			response.setFailedTriggers(failed);
			
		}
		
	return response;
	}

	private void generateAndSendPdf(EligDtlsEntity eligData , CitizenAppEntity appEntity) throws Exception {
		
		

		Document document = new Document(PageSize.A4);
		File file= new File(eligData.getCaseNum()+"pdf");

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(file);
			
			} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		PdfWriter.getInstance(document, fos);

		document.open();
		
		com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);

		Paragraph p = new Paragraph("Eligibilty Report", font);

		p.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(p);

		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 1.5f, 3.5f, 3.0f, 3.0f, 1.5f, 1.5f, 3.0f });
		table.setSpacingBefore(10);

		PdfPCell cell = new PdfPCell();

		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);

		cell.setPhrase(new Phrase(" Citizen Name", font));

		table.addCell(cell);

		cell.setPhrase(new Phrase("Plan Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Plan Status", font));

		table.addCell(cell);

		cell.setPhrase(new Phrase("Plan Start Date", font));

		table.addCell(cell);

		cell.setPhrase(new Phrase("Plan End Date", font));

		table.addCell(cell);

		cell.setPhrase(new Phrase("Benefit Amount", font));

		table.addCell(cell);
		cell.setPhrase(new Phrase("Denial Reason", font));

		table.addCell(cell);

		table.addCell(appEntity.getFullname());
		table.addCell(eligData.getPlanName());
		table.addCell(eligData.getPlanStatus());
		table.addCell(eligData.getPlanStartDate() + "");
		table.addCell(eligData.getPlanEndDate() + "");
		table.addCell(eligData.getBenefitAmt() + "");
		table.addCell(eligData.getDenialReason() + "");
		

		document.add(table);

		document.close();
		
		String subject="HIS Eligibility Info";
		String body="HIS Eligibility body";
		
		
		
		emailUtils.sendEmail(appEntity.getEmail(), subject, body, file);
		updateTrigger(eligData.getCaseNum(), file );
		file.delete();
		
		
	}

	private void updateTrigger(Long caseNum, File file) throws Exception {
		
			CoTriggerEntity coEntity = coTrgRepo.findByCaseNum(caseNum);
			byte[] arr = new byte[(byte) file.length()];

			FileInputStream fis = new FileInputStream(file);
			fis.read(arr);
			coEntity.setCoPdf(arr);
			coEntity.setTrgStatus("Completed");
			coTrgRepo.save(coEntity);
			fis.close();

		}

	}

