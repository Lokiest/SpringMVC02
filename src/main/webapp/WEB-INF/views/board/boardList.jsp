<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:import url="/top"/>

<style>
#boardBody>tr>td:nth-child(5n+2) {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
</style>

<div class="container mt-3" style="height:600px;overflow: auto;">
	<h1 class="text-center">Spring Board</h1>
	<p class="text-center my-4">
		<a href="write">글쓰기 / </a><a href="List">글목록</a>
	</p>
	
	<table class="table table-condensed table-striped">
		<thead>
			<tr>
				<th>글번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>날짜</th>
				<th>조회수</th>
			</tr>
		</thead>
		<tbody id="boardBody">
		<c:if test="${boardArr eq null or empty boardArr}">
			<tr>
				<td colspan="5"><b>No Data</b></td>
			</tr>
		</c:if>
		<c:if test="${boardArr ne null and not empty boardArr}">
			<c:forEach var="board" items="${boardArr}">
			<tr>
				<td>
					<c:out value="${board.num}" />
				</td>
				<td>
					<c:forEach var="k" begin="1" end="${board.lev}">
						&nbsp;&nbsp;&nbsp;
					</c:forEach>
					<c:if test="${board.lev>0}">
						<img src="../images/re.png">
					</c:if>
					<a href="view/<c:out value="${board.num}"/>">
					<c:if test="${fn:length(board.subject)>30}">
						<c:out value="${fn:substring(board.subject, 0,30)}" /> ...
					</c:if>
					<c:if test="${fn:length(board.subject)<=30 }">
						<c:out value="${board.subject}" />
					</c:if>
					</a>
					
					<c:if test="${board.filesize>0}">
						<span class="float-right"><img src="../images/attach.jpg" style="width:16px"
						title="<c:out value="${board.originFilename}"/>"></span>
					</c:if>
				</td>
				<td>
					<c:out value="${board.name}" />
				</td>
				<td>
					<c:out value="${board.wdate}" />
				</td>
				<td>
					<c:out value="${board.readnum}" />
				</td>
			</tr>
			</c:forEach>
		</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3" class="text-center">
				<c:forEach var="i" begin="1" end="${pageCount}">
					[<a href="list?cpage=<c:out value="${i}"/>"><c:out value="${i}" /></a>]
				</c:forEach>
				</td>
				<td colspan="2" class="text-right">
				총 게시글 수 : <c:out value="${totalCount}"/><br>
				cpage / pageCount</td>
			</tr>
		</tfoot>
	</table>
</div>

<c:import url="/foot" />