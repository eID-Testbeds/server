package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.interfaces.dao.ReportDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;
import com.secunet.eidserver.testbed.model.entities.ReportEntity;

@Stateless
public class ReportJPA extends GenericJPA<Report> implements ReportDAO
{

	public ReportJPA()
	{
		super("Report");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Report> findAll()
	{
		Collection<Report> col = entityManager.createQuery("SELECT x FROM ReportEntity x").getResultList();
		return (Set<Report>) createSetFromCollection(col);
	}

	@Override
	public Report createNew()
	{
		Report instance = new ReportEntity();
		return instance;
	}

}
