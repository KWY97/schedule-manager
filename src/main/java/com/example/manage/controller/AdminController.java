package com.example.manage.controller;

import com.example.manage.domain.Admin;
import com.example.manage.domain.Member;
import com.example.manage.domain.Schedule;
import com.example.manage.domain.ScheduleSpot;
import com.example.manage.dto.MemberEditForm;
import com.example.manage.dto.ScheduleForm;
import com.example.manage.service.AdminService;
import com.example.manage.service.MemberService;
import com.example.manage.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;
    private final ScheduleService scheduleService;

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String adminLogin(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Admin admin = adminService.login(loginId, password);

        if (admin == null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "admin/login";
        }

        session.setAttribute("loginAdminId", admin.getAdminId());

        return "redirect:/admin";
    }

    @GetMapping
    public String adminHome(HttpSession session) {
        return "admin/home";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/members")
    public String memberList(Model model) {

        List<Member> members = memberService.findAllMembers();

        model.addAttribute("members", members);

        return "admin/member-list";
    }

    @GetMapping("/members/{memberId}")
    public String memberDetail(
            @PathVariable Long memberId,
            Model model
    ) {
        Member member = memberService.findMember(memberId);

        if (member == null) {
            return "redirect:/admin/members";
        }

        model.addAttribute("member", member);

        return "admin/member-detail";
    }

    @GetMapping("/schedules/new")
    public String scheduleForm(Model model) {

        List<Member> members = memberService.findAllMembers();

        model.addAttribute("members", members);
        model.addAttribute("scheduleForm", new ScheduleForm());

        return "admin/schedule-form";
    }

    @PostMapping("/schedules/new")
    public String createSchedule(
            @Valid ScheduleForm form,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            List<Member> members = memberService.findAllMembers();
            model.addAttribute("members", members);

            return "admin/schedule-form";
        }

        scheduleService.createSchedule(
                form.getMemberId(),
                form.getScheduleDate(),
                form.getCourse(),
                form.getFirstSpotNo(),
                form.getFirstStartTime(),
                form.getSecondSpotNo(),
                form.getSecondStartTime()
        );

        return "redirect:/admin";
    }

    @GetMapping("/schedules")
    public String scheduleList(Model model) {

        List<Schedule> schedules = scheduleService.findAllSchedules();

        model.addAttribute("schedules", schedules);

        return "admin/schedule-list";
    }

    @GetMapping("/schedules/{scheduleId}")
    public String scheduleDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        Schedule schedule = scheduleService.findSchedule(scheduleId);

        if (schedule == null) {
            return "redirect:/admin/schedules";
        }

        List<ScheduleSpot> scheduleSpots = scheduleService.findScheduleSpots(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("scheduleSpots", scheduleSpots);

        return "admin/schedule-detail";
    }

    @GetMapping("/schedules/{scheduleId}/edit")
    public String editScheduleForm(
            @PathVariable Long scheduleId,
            Model model
    ) {

        Schedule schedule =
                scheduleService.findSchedule(scheduleId);

        if (schedule == null) {
            return "redirect:/admin/schedules";
        }

        List<ScheduleSpot> scheduleSpots =
                scheduleService.findScheduleSpots(scheduleId);

        ScheduleForm form = new ScheduleForm();

        form.setMemberId(schedule.getMember().getMemberId());
        form.setScheduleDate(schedule.getScheduleDate());
        form.setCourse(schedule.getCourse());

        if (scheduleSpots.size() >= 1) {
            ScheduleSpot firstSpot = scheduleSpots.get(0);

            form.setFirstSpotNo(firstSpot.getSpotNo());
            form.setFirstStartTime(firstSpot.getStartTime());
        }

        if (scheduleSpots.size() >= 2) {
            ScheduleSpot secondSpot = scheduleSpots.get(1);

            form.setSecondSpotNo(secondSpot.getSpotNo());
            form.setSecondStartTime(secondSpot.getStartTime());
        }

        List<Member> members = memberService.findAllMembers();

        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("scheduleForm", form);
        model.addAttribute("members", members);

        return "admin/schedule-edit";
    }

    @PostMapping("/schedules/{scheduleId}/edit")
    public String updateSchedule(
            @PathVariable Long scheduleId,
            @Valid ScheduleForm form,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            List<Member> members = memberService.findAllMembers();

            model.addAttribute("scheduleId", scheduleId);
            model.addAttribute("members", members);

            return "admin/schedule-edit";
        }

        scheduleService.updateSchedule(
                scheduleId,
                form.getMemberId(),
                form.getScheduleDate(),
                form.getCourse(),
                form.getFirstSpotNo(),
                form.getFirstStartTime(),
                form.getSecondSpotNo(),
                form.getSecondStartTime()
        );

        return "redirect:/admin/schedules/" + scheduleId;
    }

    @PostMapping("/schedules/{scheduleId}/delete")
    public String deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);

        return "redirect:/admin/schedules";
    }

    @GetMapping("/members/{memberId}/edit")
    public String editMemberForm(
            @PathVariable Long memberId,
            Model model
    ) {

        Member member = memberService.findMember(memberId);

        if (member == null) {
            return "redirect:/admin/members";
        }

        MemberEditForm form = new MemberEditForm();

        form.setParticipantNo(member.getParticipantNo());
        form.setGroupNo(member.getGroupNo());
        form.setLoginId(member.getLoginId());
        form.setName(member.getName());
        form.setPhone(member.getPhone());

        model.addAttribute("memberId", memberId);
        model.addAttribute("memberEditForm", form);

        return "admin/member-edit";
    }

    @PostMapping("/members/{memberId}/edit")
    public String updateMember(
            @PathVariable Long memberId,
            @Valid MemberEditForm form,
            BindingResult bindingResult,
            Model model
    ) {

        /*
         * Validation 실패
         */
        if (bindingResult.hasErrors()) {

            model.addAttribute("memberId", memberId);

            return "admin/member-edit";
        }


        String result = memberService.updateMember(
                memberId,
                form.getParticipantNo(),
                form.getGroupNo(),
                form.getLoginId(),
                form.getName(),
                form.getPhone()
        );

        /*
         * 참가자 번호가 중복된 경우
         */
        if (result.equals("PARTICIPANT_NO_DUPLICATE")) {

            model.addAttribute(
                    "errorMessage",
                    "이미 사용 중인 참가자 번호입니다."
            );

            model.addAttribute("memberId", memberId);

            return "admin/member-edit";
        }

        /*
         * 로그인 아이디가 중복된 경우
         */
        if (result.equals("LOGIN_ID_DUPLICATE")) {

            model.addAttribute(
                    "errorMessage",
                    "이미 사용 중인 로그인 아이디입니다."
            );

            model.addAttribute("memberId", memberId);

            return "admin/member-edit";
        }

        /*
         * 참가자를 찾을 수 없는 경우
         */
        if (result.equals("MEMBER_NOT_FOUND")) {
            return "redirect:/admin/members";
        }

        /*
         * 정상적으로 수정된 경우
         */
        return "redirect:/admin/members/" + memberId;
    }

    @PostMapping("/members/{memberId}/delete")
    public String deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);

        return "redirect:/admin/members";
    }
}
