package board.jpa.querydsl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import board.jpa.querydsl.dto.BoardCreateDTO;
import board.jpa.querydsl.dto.BoardDTO;
import board.jpa.querydsl.dto.BoardListDTO;
import board.jpa.querydsl.dto.BoardUpdateDTO;
import board.jpa.querydsl.service.BoardService;
import board.jpa.querydsl.util.PageRequestDTO;
import board.jpa.querydsl.util.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("spring/board/")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(final BoardService boardService) {
        log.info("Inject BoardService");
        this.boardService = boardService;
    }

    // GET | Create Board
    @GetMapping("create")
    public String getCreateBoard() {
        log.info("GET | Create Board Controller");
        return "spring/board/create";
    }

    // POST | Create Board
    @PostMapping("create")
    public String postCreateBoard(@Valid final BoardCreateDTO boardCreateDTO,
            final RedirectAttributes redirectAttributes) {
        log.info("POST | Create Board Controller");
        Long createBoard = boardService.createBoard(boardCreateDTO);
        redirectAttributes.addFlashAttribute("message", "게시물 생성 완료.");
        return "redirect:/spring/board/list";
    }

    // GET | Read Board
    @GetMapping("read/{bno}")
    public String getReadBoard(@PathVariable("bno") final Long bno, final Model model) {
        log.info("GET | Read Board Controller");
        BoardDTO list = boardService.readBoard(bno);
        model.addAttribute("list", list);
        return "spring/board/read";
    }

    // GET | List Board
    @GetMapping("list")
    public String getListBoard(final PageRequestDTO pageRequestDTO, final Model model) {
        log.info("GET | List Board Controller");
        PageResponseDTO<BoardListDTO> list = boardService.listBoard(pageRequestDTO);
        model.addAttribute("list", list);
        return "spring/board/list";
    }

    // GET | Update Board
    @GetMapping("update/{bno}")
    public String getUpdateBoard(@PathVariable("bno") final Long bno, final Model model) {
        log.info("GET | Update Board Controller");
        BoardDTO list = boardService.readBoard(bno);
        model.addAttribute("list", list);
        return "spring/board/update";
    }

    // POST | Update Board
    @PostMapping("update")
    public String postUpdateBoard(@Valid final BoardUpdateDTO boardUpdateDTO,
            final RedirectAttributes redirectAttributes) {
        log.info("POST | Update Board Controller");
        Long updateBoard = boardService.updateBoard(boardUpdateDTO);
        redirectAttributes.addFlashAttribute("message", "게시물 수정 완료.");
        return "redirect:/spring/board/read/" + boardUpdateDTO.getBno();
    }

    // POST | Delete Board
    @PostMapping("delete/{bno}")
    public String postDeleteBoard(@PathVariable("bno") final Long bno, final RedirectAttributes redirectAttributes) {
        log.info("POST | Delete Board Controller");
        Long deleteBoard = boardService.deleteBoard(bno);
        redirectAttributes.addFlashAttribute("message", "게시물 삭제 완료.");
        return "redirect:/spring/board/list";
    }
}