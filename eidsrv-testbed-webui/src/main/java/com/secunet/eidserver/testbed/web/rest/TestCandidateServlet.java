package com.secunet.eidserver.testbed.web.rest;

import com.secunet.eidserver.testbed.common.classes.Singletons;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.JavalinServlet;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

@WebServlet(name = "TestCandidateServlet", urlPatterns = {"/rest/*"})
public class TestCandidateServlet extends HttpServlet {
    @EJB
    private CandidateController candidateController;

    @EJB
    private CertificateController certificateController;

    private final JavalinServlet javalin = Javalin.createStandalone()
            .put("/rest/uploadics", ctx -> {
                Future<TestCandidate> pendingCandidate = candidateController.createCandidate(new String(ctx.bodyAsBytes()));
                TestCandidate candidate = pendingCandidate.get();
                candidate.setProfileName(ctx.queryParam("name"));
                Singletons.currentUrl = new URL(ctx.queryParam("localserver")); // there is no vaadin-request so URL isnt set if we use REST
                candidateController.saveCandidate(candidate);
            })
            .get("/rest/downloadcerts", ctx -> ctx.contentType(ContentType.APPLICATION_ZIP).result(certificateController.downloadCertificates(candidateController.getAllCandidates().stream().filter(x -> x.getProfileName().equals(ctx.queryParam("name"))).findFirst().get()).get()))
            .javalinServlet();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        javalin.service(req, resp);
    }
}
