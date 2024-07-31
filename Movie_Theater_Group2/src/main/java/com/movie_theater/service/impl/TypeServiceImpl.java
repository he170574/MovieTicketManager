package com.movie_theater.service.impl;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.dto.TypeDTO;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Type;
import com.movie_theater.entity.TypeMovie;
import com.movie_theater.repository.TypeRepository;
import com.movie_theater.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {
    private TypeRepository typeRepository;

    @Autowired
    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public List<Type> getAll() {
        return typeRepository.findAll();
    }

    @Override
    public Optional<Type> getOne(Integer id) {
        return typeRepository.findById(id);
    }

    @Override
    public Type save(Type type) {
        return typeRepository.save(type);
    }

    @Override
    public Type parseTypeDtoToType(TypeDTO typeDTO) {
        return Type.builder()
                .typeName(typeDTO.getTypeName())
                .deleted(false)
                .build();
    }

    @Override
    public TypeDTO parseTypeToTypeDto(Type type) {
        return TypeDTO.builder()
                .typeName(type.getTypeName())
                .typeId(type.getTypeId())
                .build();
    }

    @Override
    public Boolean delete(Integer id) {
        Optional<Type> optionalType = getOne(id);
        if (optionalType.isPresent()) {
            Type type = optionalType.get();
            type.setDeleted(Boolean.TRUE);
            return true;
        }
        return false;
    }
}
