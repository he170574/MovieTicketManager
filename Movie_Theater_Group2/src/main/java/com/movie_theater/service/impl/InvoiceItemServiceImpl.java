package com.movie_theater.service.impl;

import com.movie_theater.dto.InvoiceItemDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.entity.InvoiceItem;
import com.movie_theater.repository.InvoiceItemRepository;
import com.movie_theater.service.InvoiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.movie_theater.service.impl.InvoiceItemServiceImpl.InvoiceItemSpecification.*;
@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {
    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Override
    public List<InvoiceItemDTO> findInvoiceItemByAccountAndInvoiceId(Account account, Integer invoiceId) {
        return invoiceItemRepository.findInvoiceItemByAccountAndInvoiceId(account,invoiceId);
    }

    @Override
    public Page<InvoiceItemDTO> findInvoiceItemByAccount(Account account,String movieName, Pageable pageable) {
        return invoiceItemRepository.findInvoiceItemByAccountAndMovieName(account,movieName,pageable);
    }

    @Override
    public Page<InvoiceItem> findByInvoiceItemId(String invoiceItemId, Pageable pageable) {
        return invoiceItemRepository.findByInvoiceItemIdContains("%" + invoiceItemId + "%", pageable);
    }

    @Override
    public Page<InvoiceItem> findAllInvoice(Integer invoiceItemId, Integer roomId, Boolean ticketStatus, Pageable pageable) {
        Specification<InvoiceItem> spec = Specification.where(null);
        if(invoiceItemId != null) {
            spec = spec.and(withId(invoiceItemId));
        }
        if (roomId != null) {
            spec = spec.and(withRoomId(roomId));
        }
        if (ticketStatus != null) {
            spec = spec.and(withStatus(ticketStatus));
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "invoice.invoiceId"));

        return invoiceItemRepository.findAll(spec, sortedPageable);
    }

    @Override
    public InvoiceItem save(InvoiceItem invoiceItem) {
        return invoiceItemRepository.save(invoiceItem);}
    @Override
    public  int updateInvoiceStatusByInvoiceItemId(int invoiceItemId, Boolean status){
        return invoiceItemRepository.updateInvoiceStatusByInvoiceItemId(invoiceItemId,status);
    }

    public InvoiceItem getById(Integer invoiceItemId){
      return  invoiceItemRepository.getByInvoiceItemId(invoiceItemId);
    }

    interface InvoiceItemSpecification {
        static Specification<InvoiceItem> withId(Integer id) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("invoiceItemId"), id);
        }
        static Specification<InvoiceItem> withRoomId(Integer id) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("scheduleSeatHis").get("cinemaRoomId"), id);
        }
        static Specification<InvoiceItem> withStatus(Boolean status) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
        }
    }
}