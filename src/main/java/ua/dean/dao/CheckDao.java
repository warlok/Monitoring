package ua.dean.dao;

import org.springframework.stereotype.Repository;
import ua.dean.domain.Check;
import ua.dean.domain.SnmpCheck;

@Repository
public class CheckDao extends GenericDao<SnmpCheck,Long> {
}
