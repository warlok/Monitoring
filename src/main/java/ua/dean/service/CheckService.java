package ua.dean.service;

import ua.dean.domain.Check;
import ua.dean.domain.CheckResult;

import java.io.IOException;

public interface CheckService {

    CheckResult doCheck(Check check) throws IOException;

}
