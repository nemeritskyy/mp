package ua.com.obox.dbschema.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.obox.dbschema.tools.exception.ExceptionTools;
import ua.com.obox.dbschema.tools.ftp.AttachmentFTP;
import ua.com.obox.dbschema.tools.translation.CheckHeader;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFTP attachmentFTP;
    @Value("${application.image-dns}")
    private String attachmentsDns;

    public List<AttachmentResponse> getAllAttachmentsByEntityId(String entityId, String acceptLanguage) {
        List<Attachment> attachments = attachmentRepository.findAllByReferenceId(entityId);

        List<AttachmentResponse> responseList = attachments.stream()
                .map(attachment -> AttachmentResponse.builder()
                        .attachmentId(attachment.getAttachmentId())
                        .referenceId(attachment.getReferenceId())
                        .referenceType(attachment.getReferenceType())
                        .attachmentUrl(String.format("%s/%s", attachmentsDns, attachment.getAttachmentUrl()))
                        .build())
                .collect(Collectors.toList());
        return responseList;
    }

    public AttachmentResponse getAttachmentById(String attachmentId, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);
        var attachmentInfo = attachmentRepository.findByAttachmentId(attachmentId);

        Attachment attachment = attachmentInfo.orElseThrow(() -> {
            ExceptionTools.notFoundResponse(".attachmentNotFound", finalAcceptLanguage, attachmentId);
            return null;
        });

        return AttachmentResponse.builder()
                .attachmentId(attachment.getAttachmentId())
                .referenceId(attachment.getReferenceId())
                .referenceType(attachment.getReferenceType())
                .attachmentUrl(String.format("%s/%s", attachmentsDns, attachment.getAttachmentUrl()))
                .build();
    }

    public AttachmentResponseId createAttachment(Attachment request, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);
        String attachmentUUID = String.valueOf(UUID.randomUUID());
        String attachmentUrl = attachmentFTP.uploadAttachment(request.getAttachment(), request.getReferenceId(), request.getReferenceType(), attachmentUUID, finalAcceptLanguage);
        Attachment attachment = Attachment.builder()
                .attachmentId(attachmentUUID)
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .attachmentUrl(attachmentUrl)
                .build();
        attachmentRepository.save(attachment);
        return AttachmentResponseId.builder()
                .attachmentId(attachment.getAttachmentId())
                .build();
    }

    public void deleteAttachmentById(String attachmentId, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);

        var attachmentInfo = attachmentRepository.findByAttachmentId(attachmentId);

        Attachment attachment = attachmentInfo.orElseThrow(() -> {
            ExceptionTools.notFoundResponse(".attachmentNotFound", finalAcceptLanguage, attachmentId);
            return null;
        });

        AttachmentFTP.deleteAttachment(attachment.getAttachmentUrl());

        attachmentRepository.delete(attachment);
    }
}