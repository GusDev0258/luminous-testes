package com.br.luminous.service;
import com.br.luminous.entity.Address;
import com.br.luminous.entity.Device;
import com.br.luminous.exceptions.*;
import com.br.luminous.mapper.DeviceRequestToEntity;
import com.br.luminous.models.DeviceRequest;
import com.br.luminous.models.DeviceResponse;
import com.br.luminous.repository.DeviceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class DeviceService {

    private DeviceRepository deviceRepository;
    private AddressService addressService;
    private final DeviceRequestToEntity deviceRequestToEntity = new DeviceRequestToEntity();

    public List<Device> findDevicesByAddressId(Long addressId){
        Optional<List<Device>>
                devices = deviceRepository.findByAddressId(addressId);
        return devices.orElseThrow(AddressNotFoundException::new);
    }

    public Device findDeviceById(Long id){
        Optional<Device> obj = deviceRepository.findById(id);
        return obj.orElseThrow(DeviceNotFoundException::new);
    }

    public Optional<Device> findByNameAndAddressId(String deviceName, Long addressId) {
        return deviceRepository.findByAddressIdAndName(addressId, deviceName);
    }

    public void delete (Long id){
        try{
            findDeviceById(id);
            deviceRepository.deleteById(id);
        }catch(RuntimeException e){
            throw new DeviceNotFoundException();
        }
    }

    public DeviceResponse update (Long id, DeviceRequest deviceRequest, Long addressId){
        try{
            Device entity = findDeviceById(id);
            Device request = deviceRequestToEntity.mapper(deviceRequest);

            updateData(entity, request);
            deviceRepository.save(entity);

            DeviceResponse deviceResponse = new DeviceResponse();
            BeanUtils.copyProperties(entity, deviceResponse);
            return deviceResponse;

        }catch (EntityNotFoundException e){
            throw new DeviceNotFoundException();
        }
    }
    public Long create(DeviceRequest deviceRequest, Long addressId){
        Device device = deviceRequestToEntity.mapper(deviceRequest);
        Address address = updateAddressDevices(addressId, device);

        device.setAddress(address);

        Optional<Device> deviceExists = this.findByNameAndAddressId(device.getName(), addressId);
        if(deviceExists.isPresent()) {
            throw new DuplicateDeviceException();
        }

        if(device.getPower() < 0 || device.getPower() > 1500) {
            throw new DeviceInvalidPower(String.valueOf(device.getPower()));
        }

        if(device.getUsageTime().getHour() == 0) {
            throw new DeviceInvalidUsageTime(device.getUsageTime());
        }

        Device response = deviceRepository.save(device);
        deviceRepository.save(device);
        return response.getId();
    }

    private void updateData(Device device, Device request) {
        if(request.getName().trim().equals("")) {
            throw new BlankNameException();
        }

        device.setName(request.getName());
        device.setPower(request.getPower());
        device.setUsageTime(request.getUsageTime());
    }

    private Address updateAddressDevices(Long addressId, Device device){
        Address address =  addressService.getAddressById(addressId);
        List<Device> devices = address.getDevices();
        devices.add(device);
        address.setDevices(devices);
        return address;
    }
}
