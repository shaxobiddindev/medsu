package uz.medsu.sevice;

import uz.medsu.payload.SetDoctorDTO;
import uz.medsu.payload.users.UserRoleEditDTO;
import uz.medsu.utils.ResponseMessage;


public interface AdminService {
    ResponseMessage roles();
    ResponseMessage setDoctor(SetDoctorDTO userRole);
    ResponseMessage setRole(UserRoleEditDTO roleEditDTO);
    ResponseMessage getDoctor(Integer page, Integer size);
    ResponseMessage getAllUsers(Integer page, Integer size);
    ResponseMessage getAdmins(Integer page, Integer size);
    ResponseMessage getUsers(Integer page, Integer size);
    ResponseMessage blockUser(Long id);
    ResponseMessage unblockUser(Long id);
    ResponseMessage enableUser(Long id);
}
