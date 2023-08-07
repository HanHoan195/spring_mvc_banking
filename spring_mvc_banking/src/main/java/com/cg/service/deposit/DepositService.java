package com.cg.service.deposit;

import com.cg.model.Deposits;
import com.cg.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepositService implements IDepositService{
    @Autowired
    private DepositRepository depositRepository;
    @Override
    public List<Deposits> findAll() {
        return null;
    }

    @Override
    public Optional<Deposits> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Deposits save(Deposits deposits) {
        return depositRepository.save(deposits);
    }

    @Override
    public void delete(Deposits deposits) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
