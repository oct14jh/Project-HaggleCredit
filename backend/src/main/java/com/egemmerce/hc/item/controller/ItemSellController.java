package com.egemmerce.hc.item.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egemmerce.hc.alarm.service.AlarmService;
import com.egemmerce.hc.auction.service.AuctionParticipantService;
import com.egemmerce.hc.imageupload.service.ImageUploadService;
import com.egemmerce.hc.item.service.ItemDonationService;
import com.egemmerce.hc.item.service.ItemSellService;
import com.egemmerce.hc.item.service.ItemService;
import com.egemmerce.hc.repository.dto.Alarm;
import com.egemmerce.hc.repository.dto.AuctionParticipant;
import com.egemmerce.hc.repository.dto.Item;
import com.egemmerce.hc.repository.dto.ItemCtgrCnt;
import com.egemmerce.hc.repository.dto.ItemCtgrSearch;
import com.egemmerce.hc.repository.dto.ItemPhoto;
import com.egemmerce.hc.repository.dto.ItemPhotoSet;
import com.egemmerce.hc.repository.dto.ItemSell;
import com.egemmerce.hc.repository.dto.ItemSellSet;
import com.egemmerce.hc.repository.dto.SortProcess;
import com.egemmerce.hc.repository.dto.User;
import com.egemmerce.hc.repository.dto.UserAddress;
import com.egemmerce.hc.user.address.service.UserAddressService;
import com.egemmerce.hc.user.service.UserCreditService;
import com.egemmerce.hc.user.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/itemSell")
public class ItemSellController {

	@Autowired
	private ItemSellService itemSellService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private AuctionParticipantService auctionParticipantService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserAddressService userAddressService;
	@Autowired
	private UserCreditService userCreditService;
	@Autowired
	private ImageUploadService imageUploadService;
	@Autowired
	private ItemDonationService itemDonationService;
	@Autowired
	private AlarmService alarmService;

	/* C :: ?????? ?????? */
	@ApiOperation(value = "is_user_no,is_auction_price, is_category_main, is_cool_price, is_name, is_orgin_price, is_start_date, is_end_date")
	@PostMapping("/regist")
	public ResponseEntity<?> createItem(@RequestBody ItemSell itemSell) throws Exception {
		Item item = itemService.insert(Item.builder().iType("Sell").build());
		itemSell.setIsItemNo(item.getiNo());
		ItemSell check = itemSellService.insertItemSell(itemSell);
		if (check != null)
			return new ResponseEntity<ItemSell>(check, HttpStatus.OK);
		return new ResponseEntity<String>("?????? ?????? ??????", HttpStatus.NO_CONTENT);
	}

	/* R :: ?????? ???????????? */
	@GetMapping("/all")
	public ResponseEntity<Page<ItemSell>> selectItemAll(Pageable pageable) throws Exception {
		Page<ItemSell> itemSell = itemSellService.selectItemSellAll(pageable);
		System.out.println(itemSell.getSize());
		return new ResponseEntity<Page<ItemSell>>(itemSell, HttpStatus.OK);
	}

//	=============================

	/* R :: ?????????.. ?????? ?????? ?????? */
	@GetMapping("views")
	public ResponseEntity<List<ItemSellSet>> selectItemCtgr(@RequestParam(defaultValue = "1") int pageNo,
			String ctgrMain, String ctgrSub, @RequestParam(defaultValue = "ib_item_no") String sortName,
			@RequestParam(defaultValue = "down") String UD) throws Exception {
		List<ItemSellSet> itemSellSet = null;
		SortProcess sp = new SortProcess((int) (pageNo - 1) * 100, ctgrMain, ctgrSub, sortName);

		if (UD.equals("up")) { // ????????????
			if (sp.getCtgrSub() == null) {
				sp.setCtgrSub("");
				SortProcess sortProcess = new SortProcess(sp.getPageNo(), sp.getCtgrMain(), sp.getCtgrSub(),
						sp.getSortName());
				itemSellSet = itemSellService.selectItemNoSub(sortProcess);
				System.out.println("????????????????????????????????????");
			} else {
				SortProcess sortProcess = new SortProcess(sp.getPageNo(), sp.getCtgrMain(), sp.getCtgrSub(),
						sp.getSortName());
				itemSellSet = itemSellService.selectItemYesSub(sortProcess);
				System.out.println("???????????? ?????????????????????");
			}
		} else { // ????????????
			if (sp.getCtgrSub() == null) {
				sp.setCtgrSub("");
				SortProcess sortProcess = new SortProcess(sp.getPageNo(), sp.getCtgrMain(), sp.getCtgrSub(),
						sp.getSortName());
				itemSellSet = itemSellService.selectItemNoSubRvsSort(sortProcess);
				System.out.println("????????????????????????????????????");
			} else {
				SortProcess sortProcess = new SortProcess(sp.getPageNo(), sp.getCtgrMain(), sp.getCtgrSub(),
						sp.getSortName());
				itemSellSet = itemSellService.selectItemYesSubRvsSort(sortProcess);
				System.out.println("???????????? ?????????????????????");
			}
		}

		return new ResponseEntity<List<ItemSellSet>>(itemSellSet, HttpStatus.OK);
	}

	@GetMapping("viewHome")
	public ResponseEntity<List<ItemSellSet>> selectItemAllHome(@RequestParam(defaultValue = "1") int pageNo,
			String sortName, String UD) throws Exception {
		List<ItemSellSet> itemSellSet = null;
		SortProcess sp = new SortProcess((pageNo - 1) * 100, "", "", sortName);
		if (UD.equals("up")) {
			itemSellSet = itemSellService.selectItemAllHomeUp(sp);
		} else {
			itemSellSet = itemSellService.selectItemAllHomeDown(sp);
		}
		return new ResponseEntity<List<ItemSellSet>>(itemSellSet, HttpStatus.OK);
	}

	@GetMapping("cgtrCnt")
	public ResponseEntity<List<ItemCtgrCnt>> select(String ctgrMain, String ctgrSub) throws Exception {
		if (ctgrSub == null) {
			int idx = ctgrMain.length();
			ctgrSub = ctgrMain.substring(idx - 3);
			return new ResponseEntity<List<ItemCtgrCnt>>(
					itemSellService.selectCountByCtgr(new ItemCtgrSearch(ctgrMain, ctgrSub)), HttpStatus.OK);
		}
		return new ResponseEntity<List<ItemCtgrCnt>>(
				itemSellService.selectCountByCtgr(new ItemCtgrSearch(ctgrMain, ctgrSub)), HttpStatus.OK);
	}

	@GetMapping("categoryCount")
	public ResponseEntity<Integer> selectCategoryCount(String ctgrMain, String ctgrSub) throws Exception {
		List<ItemCtgrCnt> result = null;
		if (ctgrSub == null) { // ?????? ????????? ?????????..
			ctgrSub = "-";
			result = itemSellService.selectCountByCtgrSub(new ItemCtgrSearch(ctgrMain, ctgrSub));
			if (result.size() == 0) {
				System.out.println("????????? ??????????????????, ????????????????????? ??????..(??????????????????, ??????????????? ?????????");
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			if (result.get(0).getCntMain() == 0) {
				System.out.println("????????? ??????????????????, ????????????????????? ????????? ?????? 0???????");
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			return new ResponseEntity<Integer>(result.get(0).getCntMain(), HttpStatus.OK);
		} else {
			result = itemSellService.selectCountByCtgrSub(new ItemCtgrSearch(ctgrMain, ctgrSub));
			if (result.size() == 0) {
				System.out.println("????????? ????????? ?????? ??? null!!");
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
//			System.out.println("??????????????? ?????? ????????????=" + result.get(0).getIsCategorySub() + ", ????????? ??????="+result.get(0).getCntSub());
			if (result.get(0).getCntSub() == 0) {
				System.out.println("????????????????????????, ?????? ????????? ?????? ???");
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			return new ResponseEntity<Integer>(result.get(0).getCntSub(), HttpStatus.OK);
		}

	}

	/* R :: ?????? ??????(????????? ?????? ?????????) */
	@GetMapping("detail/images")
	public ResponseEntity<List<ItemPhoto>> selectItemImages(int ipItemNo) throws Exception {
		return new ResponseEntity<List<ItemPhoto>>(itemSellService.selectItemImages(ipItemNo), HttpStatus.OK);
	}
//	===============

	/* R :: ????????? ?????? */
	@GetMapping("/name")
	public ResponseEntity<Page<ItemSell>> selectItemByiName(String isName, Pageable pageable) throws Exception {
		return new ResponseEntity<Page<ItemSell>>(itemSellService.selectItemSellByisItemName(isName, pageable),
				HttpStatus.OK);
	}

	/* R :: ????????? ?????? */
	@GetMapping("/nameone")
	public ResponseEntity<?> selectoneImageItemByiName(String isName) throws Exception {
		List<ItemSell> itemSell = itemSellService.selectoneImageItemSellByisItemName(isName);
		List<ItemPhotoSet> imageItemSell = new ArrayList<>();
		for (ItemSell is : itemSell) {
			imageItemSell.add(new ItemPhotoSet(is, imageUploadService.selectItemPhotoList(is.getIsItemNo()).get(0)));
		}
		return new ResponseEntity<List<ItemPhotoSet>>(imageItemSell, HttpStatus.OK);
	}

	/* U :: ?????? ????????????(?????????) */
	@ApiOperation(value = "???????????? ??????(?????????)")
	@PutMapping("/updateDealCompleted")
	public ResponseEntity<String> updateItembyCool(int isItemNo, int uNo, int uaNo) throws Exception {
		itemService.updateItemDealCompleted(isItemNo);
		itemSellService.updateItembyCool(isItemNo, uNo, uaNo);
		Item item = itemService.selectItem(isItemNo);
		Alarm alarm = Alarm.builder()
				.aContent("????????? ?????? ?????? ????????????. ?????? ??????????????? ??????????????????.")
				.aType("sell")
				.aCause("????????????")
				.aItemNo(isItemNo)
				.aRecvUserNo(uNo)
				.aTitle(item.getItemSell().getIsItemName())
				.aItemImageValue(item.getItemPhoto().get(0).getIpValue()).build();
		alarm.generateaTime();
		alarmService.createAlarm(alarm);
		alarm = Alarm.builder()
				.aContent("????????? ????????? ?????? ?????? ???????????????. ????????????????????? ??????????????????.")
				.aType("sell")
				.aCause("????????????")
				.aItemNo(isItemNo)
				.aRecvUserNo(item.getItemSell().getIsUserNo())
				.aTitle(item.getItemSell().getIsItemName())
				.aItemImageValue(item.getItemPhoto().get(0).getIpValue()).build();
		alarm.generateaTime();
		alarmService.createAlarm(alarm);
		// UserCredit ??????
		AuctionParticipant beforeAP = auctionParticipantService.selectBeforeAP(isItemNo);
		if (beforeAP != null) {
			alarm = Alarm.builder()
					.aContent("???????????? ????????? ???????????? ????????? ?????? ???????????? ???????????????.")
					.aType("sell")
					.aCause("????????????")
					.aItemNo(isItemNo)
					.aRecvUserNo(beforeAP.getApUserNo())
					.aTitle(item.getItemSell().getIsItemName())
					.aItemImageValue(item.getItemPhoto().get(0).getIpValue()).build();
			alarm.generateaTime();
			alarmService.createAlarm(alarm);
			userService.updateUserCreditbyFail(beforeAP.getApUserNo(), beforeAP.getApBid(), isItemNo);
		}
		ItemSell itemSell = itemSellService.selectItemSellbyisItemNo(isItemNo);
//		 User ??????
		User user = userService.selectUserByuNo(uNo);
		userService.updateUserCreditbyAP(user, itemSell.getIsCoolPrice(), isItemNo);
		return new ResponseEntity<String>("????????? ??????", HttpStatus.OK);
	}

	/* U :: ?????? ????????????(?????? ??????) */
	@ApiOperation(value = "???????????? ??????(?????? ?????? ??????)")
	@PutMapping("/endAuction")
	public ResponseEntity<String> endAuction() throws Exception {

		List<ItemSell> endItemSell = itemSellService.selectOverEndDate();
		if (endItemSell.size() == 0) {
			return new ResponseEntity<String>("????????? ????????? ????????????.", HttpStatus.ACCEPTED);
		} else {
			for (ItemSell is : endItemSell) {

				itemService.updateItemDealCompleted(is.getIsItemNo());
				itemSellService.updateItembyAuction(is);

			}
			return new ResponseEntity<String>("????????? ?????? ?????? ??????", HttpStatus.ACCEPTED);
		}
	}

	/* U :: ????????? ?????? ?????? ?????? */
	@ApiOperation(value = "????????? ?????? ?????? ??????")
	@PutMapping("/updatedonation")
	public ResponseEntity<String> updateDonation() throws Exception {

		List<ItemSell> itemSellDonation = itemSellService.selectOverEndDateAndDonation();
		if (itemSellDonation.size() == 0) {
			return new ResponseEntity<String>("????????? ????????? ????????????.", HttpStatus.ACCEPTED);
		} else {
			for (ItemSell is : itemSellDonation) {
				itemDonationService.add(is);
				itemSellService.updateItembyDonation(is);

			}
			return new ResponseEntity<String>("?????? ?????? ?????? ??????", HttpStatus.ACCEPTED);
		}
	}

	/* U :: ?????? ???????????? */
	@PutMapping("/update")
	public ResponseEntity<String> updateItemSell(@RequestBody ItemSell itemSell) throws Exception {
		if (itemSellService.updateItemSell(itemSell) != null)
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		return new ResponseEntity<String>("Fail", HttpStatus.NO_CONTENT);
	}

	/* D :: ?????? ?????? */
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteItem(int isItemNo) throws Exception {
		if (itemSellService.deleteItemSell(isItemNo))
			return new ResponseEntity<String>("?????? ?????? ??????", HttpStatus.OK);
		return new ResponseEntity<String>("?????? ?????? ??????", HttpStatus.NO_CONTENT);
	}

	/* ?????? ?????? */
	@PutMapping("/auction")
	public ResponseEntity<?> updateAuction(int isUserNo, int isItemNo, int isAuctionPrice, int uaNo) throws Exception {
		// ????????? ????????? ?????? ??????
		ItemSell itemSell = itemSellService.selectItemSellbyisItemNo(isItemNo);

		// ??? ??????????????? ?????? ??????
		if (itemSell.getIsAuctionIngPrice() >= isAuctionPrice) {
			return new ResponseEntity<String>("?????? ??????????????? ????????????.", HttpStatus.OK);
		}
		// ????????? ?????? ??????
		if (itemSellService.updateAuctionPrice(isItemNo, isAuctionPrice) != null) {
			User user = userService.selectUserByuNo(isUserNo);

			// ?????? ????????? ????????????
			UserAddress userAddress = null;
			if (uaNo > 0) {
				userAddress = userAddressService.selectAddressByuaNo(uaNo);
			} else {
				userAddress = userAddressService.selectDefaultAddress(user.getuNo());
				if (userAddress == null) {
					return new ResponseEntity<String>("???????????? ????????????.", HttpStatus.OK);
				}
			}
//			// ????????? ????????? ????????? ?????? ????????? ??????
//			AuctionParticipant beforeAP = auctionParticipantService.selectBeforeAP(isItemNo);
//			if(beforeAP!=null) {
//				userService.updateUserCreditbyFail(beforeAP.getApUserNo(), beforeAP.getApBid(), isItemNo);
//			}
			userService.updateBeforeAndNew(isUserNo, isItemNo, isAuctionPrice);
//
			// ?????? ????????? ??????
			AuctionParticipant auctionParticipant = AuctionParticipant.builder().apItemNo(isItemNo).apUserNo(isUserNo)
					.apBid(isAuctionPrice).apAddress(userAddress.getUaNo()).build();
			auctionParticipant.generateapDate();
			auctionParticipantService.insert(auctionParticipant);

			User newUser = userService.selectUserByuNo(isUserNo);
//
//			// ?????? ????????? ?????? ????????? ??????
//			userService.updateUserCreditbyAP(user, isAuctionPrice, isItemNo);

			// ????????? ????????? ????????? ?????? ????????? ?????? ?????? ????????? ?????? ????????? ??????

			return new ResponseEntity<Integer>(newUser.getuCredit(), HttpStatus.OK);
		}
		return new ResponseEntity<String>("-1", HttpStatus.OK);
	}

	/* R :: ?????? ?????? ?????? */
	@ApiOperation(value = "?????? ?????? ?????? Restful API")
	@GetMapping("/myitem")
	public ResponseEntity<?> selectMyItem(int uNo) throws Exception {
		List<ItemSell> items = itemSellService.selectMyItemByuNoOnlySell(uNo);
		List<ItemPhotoSet> itemsphoto = new ArrayList<>();
		for (ItemSell is : items) {

			itemsphoto
					.add(new ItemPhotoSet(is, imageUploadService.selectItemPhotoList(is.getIsItemNo()), items.size()));
		}
		if (items != null) {
			return new ResponseEntity<List<ItemPhotoSet>>(itemsphoto, HttpStatus.OK);
		}
		return new ResponseEntity<String>("?????? ?????? ????????? ??????", HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "????????? ????????? ????????? ??????(???????????????)")
	@GetMapping("/myItemIndexing")
	public ResponseEntity<?> selectItemListIndexing(int isUserNo, @RequestParam(defaultValue = "1") int page)
			throws Exception {
		List<ItemSell> items = itemSellService.selectItemListIndexing(isUserNo, (page - 1) * 100);
		List<ItemPhotoSet> itemsphoto = new ArrayList<>();
		int itemValue = itemSellService.selectCountItemSell(isUserNo);
		for (ItemSell is : items) {
			itemsphoto.add(new ItemPhotoSet(is, imageUploadService.selectItemPhotoList(is.getIsItemNo()), itemValue));
		}
		if (items != null) {
			return new ResponseEntity<List<ItemPhotoSet>>(itemsphoto, HttpStatus.OK);
		}
		return new ResponseEntity<String>("?????? ??????", HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "????????? ?????? ?????? ??????")
	@GetMapping("/detail/inform")
	public ResponseEntity<ItemSellSet> selectItemOne(int isItemNo) throws Exception {
		ItemSellSet result = itemSellService.selectItemOne(isItemNo);
		result.setJoinerCnt(itemSellService.selectItemCntAP(isItemNo));
		return new ResponseEntity<ItemSellSet>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "????????? ??? ??????")
	@GetMapping("/count")
	public ResponseEntity<Integer> countItemSell() throws Exception {
		return new ResponseEntity<Integer>(itemSellService.countIntemSell(), HttpStatus.OK);
	}

}
