package ticketingsystem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ����
 * 
 * @author wangjialong
 *
 */
public class Route {

	int coachnum, stationnum, seatnum;

	volatile ArrayList<SectionList<SeatNode>> coaches; // ��������,Ԫ��Ϊ��λ��������

	int[] station_coach; // ÿ��վ��Ӧ�����ȳ���

	AtomicInteger[][] sold_num; // i->j �۳���Ʊ��

	public Route(int coachnum, int stationnum, int seatnum) {
		this.coachnum = coachnum;
		this.stationnum = stationnum;
		this.seatnum = seatnum;
		this.coaches = new ArrayList<SectionList<SeatNode>>(coachnum);
		for (int i = 0; i < coachnum; i++) {
			SectionList<SeatNode> list = new SectionList<SeatNode>();
			this.coaches.add(list);
		}

		this.station_coach = new int[stationnum];
		for (int i = 0; i < stationnum; i++) {
			this.station_coach[i] = i % coachnum;
		}
		this.sold_num = new AtomicInteger[stationnum + 1][stationnum + 1];
		for (int i = 0; i < stationnum + 1; i++)
			for (int j = 0; j < stationnum + 1; j++) {
				this.sold_num[i][j] = new AtomicInteger(0);
			}
	}

	public Ticket sellTicket(final long tid, String passenger, int route, int departure, int arrival) {
		// System.out.println(tid);
		if (departure >= arrival || tid == -1) {
			return null;
		}
		int coah_i = this.station_coach[arrival - 1];
		
		// ���Ƚ�����λ����
		SectionList<SeatNode> list = this.coaches.get(coah_i);
		Ticket t;
		if (list.size.get() < this.seatnum) { // ��������arrivalվ��Ӧ�ĳ�����䳵Ʊ
			int seat = list.size.get() + 1;
			if(seat > this.seatnum)
				return null;
			t = new Ticket(tid, passenger, route, coah_i + 1, seat, departure, arrival);
		} else {
			t = new Ticket(tid, passenger, route, coah_i+1, -1, departure, arrival);
		}
		if(list.sellSeat(t)) {  //����seat�Ƿ�<0������Ʊ
			sold_num[departure][arrival].getAndIncrement();
			return t;
		}
		int coah_j = this.station_coach[departure - 1];
		list = this.coaches.get(coah_j);
		if (list.size.get() < this.seatnum) { // ������departure��Ӧ������䳵Ʊ����߿ռ����λ������
			int seat = list.size.get() + 1;
			if(seat > this.seatnum)
				return null;
			t = new Ticket(tid, passenger, route, coah_j + 1, seat, departure, arrival);
		} else {
			t = new Ticket(tid, passenger, route, coah_j+1, -1, departure, arrival);
		}
		if(list.sellSeat(t)) {  //����seat�Ƿ�<0������Ʊ
			sold_num[departure][arrival].getAndIncrement();
			return t;
		}
		for (int i = 0; i < this.coachnum; i++) {
			if (i == coah_i || i == coah_j)
				continue;
			list = this.coaches.get(i);
			if (list.size.get() < this.seatnum) { // ������departure��Ӧ������䳵Ʊ����߿ռ����λ������
				int seat = list.size.get() + 1;
				if(seat > this.seatnum)
					return null;
				t = new Ticket(tid, passenger, route, i + 1, seat, departure, arrival);
			} else {
				t = new Ticket(tid, passenger, route, i+1, -1, departure, arrival);
			}
			if(list.sellSeat(t)) {
				sold_num[departure][arrival].getAndIncrement();
				return t;
			}
		}
		return null;
	}

}
