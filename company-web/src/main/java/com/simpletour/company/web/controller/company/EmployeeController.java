package com.simpletour.company.web.controller.company;

import com.simpletour.commons.data.domain.DomainPage;
import com.simpletour.commons.data.exception.BaseSystemException;
import com.simpletour.commons.util.PasswordUtil;
import com.simpletour.company.web.controller.support.*;
import com.simpletour.company.web.enums.FormModeType;
import com.simpletour.company.web.form.company.EmployeeForm;
import com.simpletour.company.web.form.system.PasswordForm;
import com.simpletour.company.web.query.company.EmployeeQuery;
import com.simpletour.domain.company.Company;
import com.simpletour.domain.company.Employee;
import com.simpletour.domain.company.Role;
import com.simpletour.service.company.ICompanyService;
import com.simpletour.service.company.IEmployeeService;
import com.simpletour.service.company.IRoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by Mario on 2016/4/11.
 */
@Controller
@RequestMapping("/company/employee/")
public class EmployeeController extends BaseController {

    private static final Logger logger = Logger.getLogger(EmployeeController.class);

    private static final String DOMAIN = "人员";
    private static final String PASSWORD = "密码";
    private static final String LIST_URL = "/company/employee/list";

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IEmployeeService employeeService;

    @Value(value = "${oms.company.employee.avatar.url}")
    private String avatarIndexUrl;

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        this.setPageTitle(model, "添加人员信息");
        this.enableGoBack(model);
        EmployeeForm employeeForm = new EmployeeForm();
        model.addAttribute("viewForm", employeeForm);
        return "/company/employee/form";
    }

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public BaseDataResponse add(@RequestBody @Valid EmployeeForm employeeForm, BindingResult bindingResult, Model model) {
        employeeForm.setMode(FormModeType.ADD.getValue());
        this.setPageTitle(model, "添加人员信息");
        this.enableGoBack(model);
        if (bindingResult.hasErrors()) {
            return BaseDataResponse.validationFail().action(BaseAction.ADD_FAIL(DOMAIN, employeeForm.getName()), false);
        }

        // TODO: 暂时先将租户ID写死
        employeeForm.setCompanyId(TokenStorage.COMPANY_ID);
        //添加默认头像地址
        if (employeeForm.getAvatar() == null || employeeForm.getAvatar().isEmpty()) {
            employeeForm.setAvatar(this.avatarIndexUrl);
        }

        try {
            Employee employee = employeeForm.as();
            Optional<Company> companyOptional = companyService.getCompanyById(employeeForm.getCompanyId());
            companyOptional.ifPresent(company -> employee.setCompany(company));
            Optional<Role> roleOptional = roleService.getRoleById(employeeForm.getRoleId());
            roleOptional.ifPresent(role -> employee.setRole(role));
            employeeService.addEmployee(employee);
        } catch (BaseSystemException e) {
            return BaseDataResponse.fail().msg(BaseAction.ADD_FAIL(DOMAIN).getTitle()).detail(e.getMessage());
        }
        return BaseDataResponse.ok().action(BaseAction.ADD_SUCCESS(DOMAIN, employeeForm.getName(), LIST_URL), true);
    }

    @RequestMapping(value = {"", "list"})
    public String list(EmployeeQuery employeeQuery, Model model) {
        this.setPageTitle(model, "人员信息列表");
        employeeQuery.setTenantId(TokenStorage.COMPANY_ID);
        DomainPage<Employee> domainPage = employeeService.queryEmployeesPagesByConditions(employeeQuery.asConditionQuery());
        model.addAttribute("query", employeeQuery);
        model.addAttribute("page", domainPage);
        model.addAttribute("pageHelper", new PageHelper(domainPage));
        return "/company/employee/list";
    }


    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Long id, Model model) {
        this.setPageTitle(model, "编辑人员信息");
        this.enableGoBack(model);
        Optional<Employee> employeeOptional = employeeService.queryEmployeeById(id);
        if (employeeOptional.isPresent()) {
            EmployeeForm employeeForm = new EmployeeForm(employeeOptional.get());
            model.addAttribute("viewForm", employeeForm);
            return "/company/employee/form";
        }
        return this.notFound();
    }

    @ResponseBody
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public BaseDataResponse edit(@RequestBody @Valid EmployeeForm employeeForm, BindingResult bindingResult, Model model) {
        employeeForm.setMode(FormModeType.UPDATE.getValue());
        this.setPageTitle(model, "编辑人员信息");
        this.enableGoBack(model);
        if (bindingResult.hasErrors()) {
            return BaseDataResponse.validationFail().action(BaseAction.ADD_FAIL(DOMAIN, employeeForm.getName()), false);
        }
        // TODO: 暂时先将租户ID写死
        employeeForm.setCompanyId(TokenStorage.COMPANY_ID);
        //添加默认头像地址
        if (employeeForm.getAvatar() == null || employeeForm.getAvatar().isEmpty()) {
            employeeForm.setAvatar(this.avatarIndexUrl);
        }
        try {
            Employee employee = employeeForm.as();
            Optional<Company> companyOptional = companyService.getCompanyById(employeeForm.getCompanyId());
            companyOptional.ifPresent(company -> employee.setCompany(company));
            Optional<Role> roleOptional = roleService.getRoleById(employeeForm.getRoleId());
            roleOptional.ifPresent(role -> employee.setRole(role));
            employeeService.updateEmployee(employee);
        } catch (BaseSystemException e) {
            return BaseDataResponse.fail().msg(BaseAction.EDIT_FAIL(DOMAIN).getTitle()).detail(e.getMessage());
        }
        return BaseDataResponse.ok().action(BaseAction.EDIT_SUCCESS(DOMAIN, employeeForm.getName(), LIST_URL), true);
    }

    @ResponseBody
    @RequestMapping(value = "delete/{id}")
    public BaseDataResponse delete(@PathVariable Long id) {
        Optional<Employee> employeeOptional;
        try {
            employeeOptional = employeeService.queryEmployeeById(id);
            if (!employeeOptional.isPresent()) {
                return BaseDataResponse.fail().action(BaseAction.OBJECT_NOTFOUND(), false);
            }
        } catch (BaseSystemException e) {
            return BaseDataResponse.fail().msg(BaseAction.DEL_FAIL(DOMAIN).getTitle()).detail(e.getMessage());
        }
        try {
            employeeService.deleteEmployee(id);
        } catch (BaseSystemException e) {
            return BaseDataResponse.fail().msg(BaseAction.DEL_FAIL(DOMAIN).getTitle()).detail(e.getMessage());
        }
        return BaseDataResponse.ok().action(BaseAction.DEL_SUCCESS(DOMAIN, employeeOptional.get().getName(), LIST_URL), true);
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String password(Model model) {
        this.setPageTitle(model, "修改密码");
        this.enableGoBack(model);
        EmployeeQuery employeeQuery = new EmployeeQuery();
        employeeQuery.setTenantId(TokenStorage.COMPANY_ID);
        DomainPage<Employee> domainPage = employeeService.queryEmployeesPagesByConditions(employeeQuery.asConditionQuery());
        if (null == domainPage || 0 == domainPage.getDomainTotalCount()) {
            new BaseSystemException("用户不存在");
        }
        model.addAttribute("viewForm", new EmployeeForm());

        return "/company/employee/password";
    }

    @ResponseBody
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public BaseDataResponse password(@RequestBody @Valid PasswordForm passwordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return BaseDataResponse.validationFail().action(BaseAction.EDIT_FAIL(PASSWORD), false);
        }

        //TODO...后面最好修改为用jsr303去校验两次密码不一致
        //校验新密码和确认密码是否一致
        if (!passwordForm.getPassword().equals(passwordForm.getRepeatPassword()))
            return BaseDataResponse.validationFail().action(BaseAction.EDIT_FAIL(PASSWORD), false);

//        Optional<Employee> employeeOptionalBefore = employeeService.queryEmployeeById(getCurrentUser().getId());
//        if (!employeeOptionalBefore.isPresent())
//            return BaseDataResponse.fail().msg("用户不存在").detail("请重新登录");

        // TODO: 暂时先将租户ID写死
        TokenStorage.setLocalTokenWithCompanyId(0L);

        Optional<Employee> employeeOptionalBefore = employeeService.queryEmployeeById(passwordForm.getId());
        if (!employeeOptionalBefore.isPresent() || !employeeOptionalBefore.get().getCompany().getId().equals(TokenStorage.COMPANY_ID))
            return BaseDataResponse.fail().msg("用户不存在").detail("请重新登录");

        //验证旧密码是否为正确的密码
        String oldpwd = PasswordUtil.getMd5Password(passwordForm.getOldPassword(), employeeOptionalBefore.get().getSalt());
        if (!employeeOptionalBefore.get().getPasswd().equals(oldpwd)) {
            return BaseDataResponse.fail().msg("修改用户密码失败").detail("'" + employeeOptionalBefore.get().getName() + "'用户密码验证出错");
        }
        String newSalt = PasswordUtil.generateSalt();
        String newPassword = PasswordUtil.getMd5Password(passwordForm.getPassword(), newSalt);
        try {
            employeeService.changePwdForEmployee(getCurrentUser().getId(), newPassword, newSalt);
        } catch (BaseSystemException e) {
            return BaseDataResponse.fail().msg("修改用户密码失败").detail(e.getMessage());
        }
        return BaseDataResponse.ok().msg("修改用户密码成功").detail("修改'" + employeeOptionalBefore.get().getName() + "'用户密码成功");
    }
}
