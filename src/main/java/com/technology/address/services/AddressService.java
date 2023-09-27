package com.technology.address.services;

import com.technology.address.models.Address;
import com.technology.address.regitration.request.AddressRegistrationRequest;

public interface AddressService {
    Address registerUserAddress(AddressRegistrationRequest addressRegistrationRequest);
}
