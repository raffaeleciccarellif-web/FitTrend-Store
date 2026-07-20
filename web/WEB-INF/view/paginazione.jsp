<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="paginaCorrente" type="java.lang.Integer"--%>
<%--@elvariable id="totalePagine" type="java.lang.Integer"--%>
<%--@elvariable id="baseUrl" type="java.lang.String"--%>

<c:if test="${totalePagine > 1}">
    <div class="pagination">
        <%-- Pulsante Precedente --%>
        <c:choose>
            <c:when test="${paginaCorrente > 1}">
                <a href="${baseUrl}${baseUrl.contains('?') ? '&' : '?'}page=${paginaCorrente - 1}" class="btn btn-sm btn-secondary">&laquo; Precedente</a>
            </c:when>
            <c:otherwise>
                <span class="btn btn-sm btn-secondary disabled">&laquo; Precedente</span>
            </c:otherwise>
        </c:choose>

        <%-- Numeri di pagina --%>
        <div class="pagination-numbers">
            <c:forEach begin="1" end="${totalePagine}" var="i">
                <c:choose>
                    <c:when test="${i == paginaCorrente}">
                        <span class="pagination-number active"><c:out value="${i}"/></span>
                    </c:when>
                    <c:otherwise>
                        <a href="${baseUrl}${baseUrl.contains('?') ? '&' : '?'}page=${i}" class="pagination-number"><c:out value="${i}"/></a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>

        <%-- Pulsante Successiva --%>
        <c:choose>
            <c:when test="${paginaCorrente < totalePagine}">
                <a href="${baseUrl}${baseUrl.contains('?') ? '&' : '?'}page=${paginaCorrente + 1}" class="btn btn-sm btn-secondary">Successiva &raquo;</a>
            </c:when>
            <c:otherwise>
                <span class="btn btn-sm btn-secondary disabled">Successiva &raquo;</span>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
