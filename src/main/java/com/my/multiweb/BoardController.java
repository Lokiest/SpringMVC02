package com.my.multiweb;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.board.model.BoardVO;
import com.board.model.PagingVO;
import com.board.service.BoardService;
import com.common.CommonUtil;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/board")
@Log4j
public class BoardController {
	
	@Resource(name="boardServiceImpl")
	private BoardService boardService;
	
	@GetMapping("/write")
	public String boardForm() {
		
		return "board/boardWrite";
	}
	
	@Inject
	private CommonUtil util;
	
	@PostMapping("/write")
	public String boardInsert(Model m, HttpServletRequest req, 
			@RequestParam("mfilename") MultipartFile mfilename, @ModelAttribute BoardVO board) {
		log.info("board == " + board);
		//1. 파일 업로드 처리
		ServletContext app = req.getServletContext();
		String upDir = app.getRealPath("/resources/board_upload");
		File dir = new File(upDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		if(!mfilename.isEmpty()) {
			String originFname = mfilename.getOriginalFilename();
			long fsize = mfilename.getSize();
			log.info(originFname + " >>> " + fsize);
			
			// 동일 파일명이 서버에 있을 경우 덮어쓰기 방지 위해 랜덤문자열_원본파일명 >> 물리적 파일명 생성
			UUID uuid = UUID.randomUUID();
			String filename = uuid.toString() + "_" + originFname;
			log.info("filename == " + filename);
			
			//3-1 mode가 수정이고 예전에 첨부했던 파일이 있다면 예전 파일 삭제 처리
			if(board.getMode().equals("edit")&& board.getOld_filename()!=null) {
				//수정모드라면 예전에 업로드했던 파일은 삭제 처리
				File delF = new File(upDir, board.getOld_filename());
				if(delF.exists()) {
					boolean b = delF.delete();
					log.info("old file 삭제여부 == " + b);
				}
			}
			
			//3-2 업로드 처리
			
			
			try {
				mfilename.transferTo(new File(upDir, filename));
				log.info("upDir == " + upDir);
			} catch(Exception e) {
				log.error("board write file upload error "+e);
			}
			
			board.setFilename(filename);
			board.setOriginFilename(originFname);
			board.setFilesize(fsize);
		}
		
		//2. 유효성 체크 (subject, name, passwd) >> redirect "write"
		if(board.getName()==null||board.getSubject()==null||board.getPasswd()==null||
				board.getName().trim().isEmpty()||board.getSubject().trim().isEmpty()||board.getPasswd().trim().isEmpty()) {
			return "redirect:write";
		}
		
		//3. boardService의 insertBoard() 호출
		int n = 0;
		String str = "", loc = "";
		if("write".equals(board.getMode())) {
//			for(int i=0;i<30;i++)
			n = this.boardService.insertBoard(board);
			
			str = "글쓰기 ";
			
		} else if("rewrite".equals(board.getMode())) {
			n=this.boardService.rewriteBoard(board);
			str="답변 글쓰기 ";
		} else if("edit".equals(board.getMode())) {
			n = this.boardService.updateBoard(board);
			str="글수정 ";
		}
		str+=(n>0)?"성공":"실패";
		loc+=(n>0)?"list":"javascript:history.back()";
		
		//4. message, loc 저장
		
		
//		m.addAttribute("message", "test");
//		m.addAttribute("loc", "list");
		//반복되는 부분 컴포넌트 만들어서 처리하기
		
		return util.addMsgLoc(m, str, loc);
	}
	
	@GetMapping("/list")
	public String boardListPaging(Model m, @ModelAttribute("page") PagingVO page, HttpServletRequest req,
			@RequestHeader("User-Agent") String userAgent) {
		String myctx = req.getContextPath();
		
		HttpSession ses = req.getSession();
		
		//1 총 게시글 수 가져오기
		log.info("1 page == " + page);
		int totlaCount = this.boardService.getTotalCount(page);
		page.setTotalCount(totlaCount);
		//page.setPageSize(5);
		page.setPagingBlock(5);
		
		page.init(ses);
		
		log.info("2.page == " +page);
		//2 게시글 목록 가져오기
		List<BoardVO> boardArr = this.boardService.selectBoardAllPaging(page);
		
		//3 페이지 네비게이션 문자열 받아오기;
		String loc = "board/list";
		String pageNavi = page.getPageNavi(myctx, loc, userAgent);
		
		m.addAttribute("pageNavi", pageNavi);
		m.addAttribute("paging", page);
		m.addAttribute("boardArr", boardArr);
		
		return "board/boardList3";
	}
	
	@GetMapping("/list_old")
	public String boardList(Model m, @RequestParam(defaultValue = "1") int cpage) {
		if(cpage<=0) {
			cpage=1;
		}
		
		//1 총 게시글 수 가져오기
		int totalCount = this.boardService.getTotalCount(null);
		
		//2 한 페이지당 보여줄 목록 개수 정하기
		int pageSize=5;
		int pageCount = (totalCount - 1) / pageSize + 1;
		
		if(cpage>pageCount) {
			cpage=pageCount;
		}
		
		int start = (cpage-1)*pageSize;
		int end = start + (pageSize+1);
		
		Map<String, Integer> map = new HashMap<>();
		map.put("start", Integer.valueOf(start));
		map.put("end", Integer.valueOf(end));
		
		//게시판 목록 가져와서 모델에 저장
		//"boardArr"
		List<BoardVO> boardArr = this.boardService.selectBoardAll(map);
		m.addAttribute("boardArr", boardArr);
		m.addAttribute("totalCount", totalCount);
		m.addAttribute("pageCount", pageCount);
		m.addAttribute("cpage", cpage);
		
		return "board/boardList";
	}
	
	
	@GetMapping("/view/{num}")
	public String boardView(Model m, @PathVariable("num") int num) {
		log.info("num===" + num);
		//조회수 증가 처리
		int n = this.boardService.updateReadnum(num);
		
		BoardVO board = this.boardService.selectBoardByIdx(num);
		m.addAttribute("board", board);
		
		return "board/boardView";
	}
	
	@PostMapping("/delete")
	public String boardDelete(Model m, @RequestParam(defaultValue = "0") int num,
			@RequestParam(defaultValue = "") String passwd, HttpServletRequest req) {
		log.info("num == " + num + ",  passwd == " + passwd);
		if(num==0||passwd.isEmpty()) {
			return "redirect:list";
		}
		//해당 글 db에서 가져오기
		BoardVO vo = this.boardService.selectBoardByIdx(num);
		if(vo==null) {
			return util.addMsgBack(m, "no data");
		}
		
		//비밀번호 일치여부 체크
		String dbPwd = vo.getPasswd();
		if(!dbPwd.equals(passwd)) {
			return util.addMsgBack(m, "비밀번호 오류");
		}
		
		//db에서 글 삭제처리
		int n = this.boardService.deleteBoard(num);
		ServletContext app = req.getServletContext();
		String upDir = app.getRealPath("/resources/board_upload");
		log.info("upDir == " + upDir);
		
		//서버에 업로드한 첨부파일이 있다면 서버에서 삭제 처리
		if(n>0 && vo.getFilename()!=null) {
			File f = new File(upDir, vo.getFilename());
			if(f.exists()) {
				boolean b = f.delete();
				log.info("파일 삭제 여부 : " + b);
			}
		}
		String str = (n>0)?"성공":"실패";
		String loc = (n>0)?"list":"javascript:history.back()";
		
		return util.addMsgLoc(m, str, loc);
	}
	
	@PostMapping("/edit")
	public String boardEditform(Model m,
			@RequestParam(defaultValue = "0") int num, @RequestParam(defaultValue = "") String passwd) {
		//1 유효성체크 >> list redirect로 이동
		if(num==0||passwd.isEmpty()) {
			return "redirect:list";
		}
		
		//2 글 번호로 해당 글 내용 가져오기, 없으면 되돌려보내고
		BoardVO vo = this.boardService.selectBoardByIdx(num);
		if(vo==null) {
			return util.addMsgBack(m, "no data");
		}
		
		//3 비번 체크
		if(!vo.getPasswd().equals(passwd)) {
			return util.addMsgBack(m, "password error");
		}
		
		//4 Model에 해당 글 저장 "board"
		m.addAttribute("board", vo);
		
		return "board/boardEdit";
	}
	
	@PostMapping("/rewrite")
	public String boardRewrite(Model m, @ModelAttribute BoardVO vo) {
		log.info("vo == " + vo);
		m.addAttribute("num", vo.getNum());
		m.addAttribute("subject", vo.getSubject());
		
		return "board/boardRewrite";
	}
	
	
	
}
