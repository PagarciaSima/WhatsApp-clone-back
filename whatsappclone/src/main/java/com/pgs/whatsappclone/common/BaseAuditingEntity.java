package com.pgs.whatsappclone.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Builder with inheritance
@SuperBuilder
// Extend auditing 
@MappedSuperclass
// Enable auditing (also in main method)
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditingEntity {

	@CreatedDate
	@Column(name ="created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;
	
	@LastModifiedDate
	@Column(name ="last_modified_date", insertable = false)
	private LocalDateTime lastModifiedDate;
	
	
}
