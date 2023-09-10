package com.technology.registration.services.address;

import com.technology.registration.models.Address;
import com.technology.registration.registration.requests.address.AddressRegistrationRequest;
import com.technology.registration.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public Address registerUserAddress(AddressRegistrationRequest addressRegistrationRequest) {
        Address address = Address.builder()
                .setPhoneNumber(addressRegistrationRequest.phoneNumber()
                )
                .setRegion(addressRegistrationRequest.region())
                .setDistrict(addressRegistrationRequest.district())
                .setLocality(addressRegistrationRequest.locality())
                .setStreet(addressRegistrationRequest.street())
                .setPremise(addressRegistrationRequest.premise())
                .setZipcode(addressRegistrationRequest.zipcode())
                .build();
        addressRepository.save(address);
        return address;
    }
}
