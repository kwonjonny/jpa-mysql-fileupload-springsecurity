package board.jpa.querydsl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import board.jpa.querydsl.domain.board.BoardEntity;
import board.jpa.querydsl.domain.reply.ReplyEntity;
import board.jpa.querydsl.dto.reply.ReplyCreateDTO;
import board.jpa.querydsl.dto.reply.ReplyDTO;
import board.jpa.querydsl.dto.reply.ReplyListDTO;
import board.jpa.querydsl.dto.reply.ReplyUpdateDTO;
import board.jpa.querydsl.exception.BoardNumberNotFoundException;
import board.jpa.querydsl.exception.DataNotFoundException;
import board.jpa.querydsl.exception.ReplyNumberNotFoundException;
import board.jpa.querydsl.exception.errorcode.ReplyErrorMessage;
import board.jpa.querydsl.repository.BoardRepository;
import board.jpa.querydsl.repository.ReplyRepository;
import board.jpa.querydsl.service.ReplyService;
import board.jpa.querydsl.util.page.PageRequestDTO;
import board.jpa.querydsl.util.page.PageResponseDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public ReplyServiceImpl(final ReplyRepository replyRepository, final BoardRepository boardRepository) {
        log.info("Inject ReplyRepository, BoardRepository");
        this.replyRepository = replyRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    @Transactional
    public Long createReply(final ReplyCreateDTO replyCreateDTO) {
        validationBoardNumber(replyCreateDTO.getBno());
        createReplyValidationData(replyCreateDTO);
        if (replyCreateDTO.getGno() == null || replyCreateDTO.getGno() == 0L) {
            final ReplyEntity replyEntity = ReplyEntity.createReply(
                    replyCreateDTO.getBno(),
                    replyCreateDTO.getReply(),
                    replyCreateDTO.getReplyer(),
                    replyCreateDTO.getGno(),
                    replyCreateDTO.getCreateDate(),
                    replyCreateDTO.getUpdateDate(),
                    replyCreateDTO.getIsDeleted());
            final BoardEntity boardEntity = boardRepository.findByBno(replyCreateDTO.getBno())
                    .orElse(null);
            boardEntity.incremnetReplyCount();
            boardRepository.save(boardEntity);
            replyRepository.save(replyEntity);
            replyRepository.updateGno(replyEntity.getRno());
            return replyEntity.getRno();
        } else {
            final ReplyEntity replyEntity = ReplyEntity.createReply(
                    replyCreateDTO.getBno(),
                    replyCreateDTO.getReply(),
                    replyCreateDTO.getReplyer(),
                    replyCreateDTO.getGno(),
                    replyCreateDTO.getCreateDate(),
                    replyCreateDTO.getUpdateDate(),
                    replyCreateDTO.getIsDeleted());
            final BoardEntity boardEntity = boardRepository.findByBno(replyCreateDTO.getBno())
                    .orElse(null);
            boardEntity.incremnetReplyCount();
            boardRepository.save(boardEntity);
            replyRepository.save(replyEntity);
            return replyEntity.getRno();
        }
    }

    @Transactional(readOnly = true)
    private void createReplyValidationData(final ReplyCreateDTO replyCreateDTO) {
        if (replyCreateDTO.getBno() == null ||
                replyCreateDTO.getReply() == null ||
                replyCreateDTO.getReplyer() == null) {
            throw new DataNotFoundException(ReplyErrorMessage.DATA_NOT_FOUND.getMessage());
        }
    }

    @Transactional(readOnly = true)
    private void updateReplyValidationData(final ReplyUpdateDTO replyUpdateDTO) {
        if (replyUpdateDTO.getBno() == null ||
                replyUpdateDTO.getReply() == null ||
                replyUpdateDTO.getReplyer() == null ||
                replyUpdateDTO.getRno() == null) {
            throw new DataNotFoundException(ReplyErrorMessage.DATA_NOT_FOUND.getMessage());
        }
    }

    @Transactional(readOnly = true)
    private void validationBoardNumber(final Long bno) {
        final BoardEntity boardEntity = boardRepository.findByBno(bno)
                .orElseThrow(() -> new BoardNumberNotFoundException(
                        String.format(ReplyErrorMessage.BOARD_NUMBER_NOT_FOUND.getFormattedMessage(bno))));
    }

    @Transactional(readOnly = true)
    private void validationReplyNumber(final Long rno) {
        final ReplyEntity replyEntity = replyRepository.findById(rno)
                .orElseThrow(() -> new ReplyNumberNotFoundException(
                        String.format(ReplyErrorMessage.REPLY_NUMBER_NOT_FOUND.getFormattedMessage(rno))));
    }

    @Override
    @Transactional
    public ReplyDTO readReply(final Long rno) {
        validationReplyNumber(rno);
        final ReplyEntity replyEntity = replyRepository.findById(rno)
                .orElse(null);
        final ReplyDTO replyDTO = ReplyDTO
                .builder()
                .bno(replyEntity.getBno())
                .rno(replyEntity.getRno())
                .gno(replyEntity.getGno())
                .isDeleted(replyEntity.getIsDeleted())
                .reply(replyEntity.getReply())
                .replyer(replyEntity.getReplyer())
                .createDate(replyEntity.getCreateDate())
                .updateDate(replyEntity.getUpdateDate())
                .build();
        return replyDTO;
    }

    @Override
    @Transactional
    public Long deleteReply(Long rno) {
        validationReplyNumber(rno);
        final ReplyEntity replyEntity = replyRepository.findById(rno)
                .orElse(null);
        final BoardEntity boardEntity = boardRepository.findByBno(replyEntity.getBno())
                .orElse(null);
        validationBoardNumber(boardEntity.getBno());
        replyEntity.deleteReply();
        replyRepository.save(replyEntity);
        boardEntity.decremnetReplyCount();
        boardRepository.save(boardEntity);
        return replyEntity.getRno();
    }

    @Override
    @Transactional
    public PageResponseDTO<ReplyListDTO> listReply(PageRequestDTO pageRequestDTO, Long bno) {
        validationBoardNumber(bno);
        return replyRepository.listReply(pageRequestDTO, bno);
    }

    @Override
    @Transactional
    public Long updateReply(final ReplyUpdateDTO replyUpdateDTO) {
        validationReplyNumber(replyUpdateDTO.getRno());
        validationBoardNumber(replyUpdateDTO.getBno());
        final ReplyEntity replyEntity = replyRepository.findById(replyUpdateDTO.getRno())
                .orElse(null);
        replyEntity.updateReply(replyUpdateDTO.getBno(),
                replyUpdateDTO.getRno(),
                replyUpdateDTO.getReply(),
                replyUpdateDTO.getReplyer(),
                replyUpdateDTO.getGno(),
                replyUpdateDTO.getUpdateDate(),
                replyUpdateDTO.getGno());
        return replyEntity.getRno();
    }
}