package com.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.mapper.BoardMapper;
import com.board.model.BoardVO;
import com.board.model.PagingVO;

@Service("boardServiceImpl")
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private BoardMapper boardMapper;

	@Override
	public int insertBoard(BoardVO board) {
		// TODO Auto-generated method stub
		return this.boardMapper.insertBoard(board);
	}

	@Override
	public List<BoardVO> selectBoardAll(Map<String, Integer> map) {
		// TODO Auto-generated method stub
		return this.boardMapper.selectBoardAll(map);
	}

	@Override
	public List<BoardVO> selectBoardAllPaging(PagingVO paging) {
		// TODO Auto-generated method stub
		return this.boardMapper.selectBoardAllPaging(paging);
	}

	@Override
	public List<BoardVO> findBoard(PagingVO paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalCount(PagingVO paging) {
		// TODO Auto-generated method stub
		return this.boardMapper.getTotalCount(paging);
	}

	@Override
	public BoardVO selectBoardByIdx(Integer num) {
		// TODO Auto-generated method stub
		return this.boardMapper.selectBoardByIdx(num);
	}

	@Override
	public int updateReadnum(Integer idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String selectPwd(Integer idx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteBoard(Integer idx) {
		// TODO Auto-generated method stub
		return this.boardMapper.deleteBoard(idx);
	}

	@Override
	public int updateBoard(BoardVO board) {
		// TODO Auto-generated method stub
		return this.boardMapper.updateBoard(board);
	}

	@Override
	public int rewriteBoard(BoardVO board) {
		//1 부모글의 글번호로 부모글의 refer, lev, sunbun 가져오기
		BoardVO parent = this.selectRefLevSunbun(board.getNum());
		
		//2 기존에 달린 답변글들이 있으면 내 답변글 insert하기 전에 기존의 답변글들의 순번을 하나씩 증가시키기 >> update문
		int n = this.updateSunbun(parent);
		
		//3 내가 쓴 답변 글을 insert >> insert문
		board.setRefer(parent.getRefer());
		board.setLev(parent.getLev() + 1);
		board.setSunbun(parent.getSunbun() + 1);
		
		return this.boardMapper.rewriteBoard(board);
	}

	@Override
	public BoardVO selectRefLevSunbun(int idx) {
		// TODO Auto-generated method stub
		return this.boardMapper.selectRefLevSunbun(idx);
	}

	@Override
	public int updateSunbun(BoardVO parent) {
		// TODO Auto-generated method stub
		return this.boardMapper.updateSunbun(parent);
	}

}
