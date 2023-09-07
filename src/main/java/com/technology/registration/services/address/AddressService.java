package com.technology.registration.services.address;

import com.technology.registration.models.Address;
import com.technology.registration.registration.requests.address.AddressRegistrationRequest;

public interface AddressService {
    Address registerUserAddress(AddressRegistrationRequest addressRegistrationRequest);
}
