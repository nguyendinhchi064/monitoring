package Connections.Resources;

import Connections.Model.JobInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class JobInfoRepository {

    @Inject
    EntityManager em;

    @Transactional
    public void saveJob(JobInfo job) {
        em.merge(job);
    }

    public JobInfo findJobById(String jobId) {
        return em.find(JobInfo.class, jobId);
    }

    @Transactional
    public void updateJob(JobInfo job) {
        em.merge(job);
    }

    public List<JobInfo> findJobsByUsername(String username) {
        return em.createQuery("SELECT j FROM JobInfo j WHERE j.username = :username", JobInfo.class)
                .setParameter("username", username)
                .getResultList();
    }
}
