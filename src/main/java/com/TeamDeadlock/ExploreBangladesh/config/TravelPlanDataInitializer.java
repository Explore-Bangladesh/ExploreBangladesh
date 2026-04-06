package com.TeamDeadlock.ExploreBangladesh.config;

import com.TeamDeadlock.ExploreBangladesh.entity.*;
import com.TeamDeadlock.ExploreBangladesh.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class TravelPlanDataInitializer implements ApplicationRunner {

    private final TravelPlanEntityRepository planRepo;
    private final ItineraryDayRepository dayRepo;
    private final CostBreakdownRepository costRepo;

    public TravelPlanDataInitializer(TravelPlanEntityRepository planRepo,
                                      ItineraryDayRepository dayRepo,
                                      CostBreakdownRepository costRepo) {
        this.planRepo = planRepo;
        this.dayRepo = dayRepo;
        this.costRepo = costRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (planRepo.count() > 0) return;
        seedBudgetPlans();
        seedStandardPlans();
        seedPremiumPlans();
        System.out.println("[TravelPlanDataInitializer] Seeded " + planRepo.count() + " travel plans.");
    }

    // ── helpers ──
    private TravelPlanEntity plan(String id, String dest, String img, String tier,
                                   int days, int nights, double cost, String desc,
                                   String best, String group, String div, String... hl) {
        return new TravelPlanEntity(id, dest, img, tier, days, nights, cost, desc, best, group, div, Arrays.asList(hl));
    }

    private ItineraryDayEntity day(TravelPlanEntity p, int num, String title, String summary) {
        return new ItineraryDayEntity(p, num, title, summary);
    }

    private ItineraryActivityEntity act(ItineraryDayEntity d, String start, String end,
                                         String title, String desc, String type,
                                         String loc, double cost, String tips, int order) {
        return new ItineraryActivityEntity(d, start, end, title, desc, type, loc, cost, tips, order);
    }

    private CostBreakdownEntity cb(TravelPlanEntity p, String cat, double amt, String desc) {
        return new CostBreakdownEntity(p, cat, amt, desc);
    }

    // ════════════════════════════════════════════
    //  BUDGET TIER (8 plans, one per division)
    // ════════════════════════════════════════════
    private void seedBudgetPlans() {
        // 1. Chattogram – Cox's Bazar
        TravelPlanEntity p1 = planRepo.save(plan("bud-ctg", "Cox's Bazar Beach Escape",
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
            "BUDGET", 3, 2, 5500,
            "Experience the world's longest sea beach on a shoestring. Stay in budget guesthouses, eat fresh seafood at local stalls, and explore Laboni Point, Himchari and Inani Beach by CNG auto-rickshaw.",
            "October – March", "1–4 persons", "Chattogram",
            "Longest Sea Beach", "Budget Seafood", "Himchari Waterfall", "Inani Coral Beach"));
        costRepo.saveAll(List.of(
            cb(p1,"TRANSPORT",1500,"Bus from Dhaka + local CNG"),
            cb(p1,"ACCOMMODATION",2000,"Budget guesthouse 2 nights"),
            cb(p1,"FOOD",1200,"Local restaurants & street food"),
            cb(p1,"ACTIVITIES",500,"Himchari entry + beach activities"),
            cb(p1,"MISCELLANEOUS",300,"Tips & misc")));
        // Day 1
        ItineraryDayEntity d1 = dayRepo.save(day(p1, 1, "Journey & Beach Arrival", "Travel from Dhaka, arrive Cox's Bazar, explore Laboni Beach at sunset."));
        d1.setActivities(List.of(
            act(d1,"06:00","06:30","Wake Up & Pack","Get ready for departure","REST","Dhaka",0,"Pack light, carry sunscreen",1),
            act(d1,"07:00","07:30","Breakfast","Quick breakfast before bus","FOOD","Dhaka",80,"Eat at local hotel near bus stand",2),
            act(d1,"08:00","18:00","Bus to Cox's Bazar","Non-AC bus from Sayedabad/Fakirapul to Cox's Bazar","TRAVEL","Dhaka → Cox's Bazar",900,"Book Shyamoli/Hanif. Carry snacks for journey",3),
            act(d1,"18:00","18:30","Check-in","Check into budget guesthouse near Laboni Point","REST","Kolatoli, Cox's Bazar",1000,"Negotiate rate, check room before paying",4),
            act(d1,"18:30","19:30","Laboni Beach Sunset","Walk along Laboni Beach and enjoy the sunset","SIGHTSEEING","Laboni Point",0,"Best sunset spot near the main beach gate",5),
            act(d1,"20:00","21:00","Dinner","Fresh seafood dinner at local restaurant","FOOD","Kolatoli Road",200,"Try grilled pomfret or prawn curry",6),
            act(d1,"21:30","22:00","Night Walk","Stroll along the illuminated beach road","ACTIVITY","Beach Road",0,"Buy souvenirs from beach stalls",7)));
        dayRepo.save(d1);
        // Day 2
        ItineraryDayEntity d2 = dayRepo.save(day(p1, 2, "Himchari & Inani Exploration", "Visit Himchari National Park waterfall and the serene Inani coral beach."));
        d2.setActivities(List.of(
            act(d2,"06:30","07:00","Sunrise at Beach","Early morning beach walk for sunrise","SIGHTSEEING","Laboni Beach",0,"Sunrise around 6:15 AM in winter",1),
            act(d2,"07:30","08:30","Breakfast","Breakfast at guesthouse or nearby stall","FOOD","Kolatoli",100,"Try paratha with egg curry",2),
            act(d2,"09:00","11:00","Himchari National Park","Trek to Himchari waterfall, enjoy panoramic ocean views from cliffs","SIGHTSEEING","Himchari, 12km south",250,"Hire CNG (₹150 round trip). Waterfall best in monsoon",3),
            act(d2,"11:30","14:00","Inani Beach","Visit the pristine coral stone beach, swim in clear water","SIGHTSEEING","Inani, 27km south",0,"Less crowded than Laboni. Bring drinking water",4),
            act(d2,"14:00","15:00","Lunch","Seafood lunch at Inani beachside restaurant","FOOD","Inani Beach",250,"Fresh fish is cheapest here",5),
            act(d2,"15:30","17:00","Marine Drive","Ride along the scenic Cox's Bazar Marine Drive","SIGHTSEEING","Marine Drive Road",100,"CNG ride along the coast, stop for photos",6),
            act(d2,"17:30","19:00","Burmese Market","Shop for souvenirs, dried fish, and local handicrafts","ACTIVITY","Burmese Market",200,"Bargain hard – start at half the asking price",7),
            act(d2,"19:30","20:30","Dinner","Dinner at a local restaurant","FOOD","Kolatoli",200,"Try shutki (dried fish) dishes",8)));
        dayRepo.save(d2);
        // Day 3
        ItineraryDayEntity d3 = dayRepo.save(day(p1, 3, "Last Morning & Return", "Enjoy a final beach morning, then travel back to Dhaka."));
        d3.setActivities(List.of(
            act(d3,"06:00","07:00","Beach Morning","Last beach walk, take photos","SIGHTSEEING","Laboni Beach",0,"Best light for photos early morning",1),
            act(d3,"07:30","08:30","Breakfast & Checkout","Final breakfast, pack and check out","FOOD","Kolatoli",100,"",2),
            act(d3,"09:00","09:30","Local Market Visit","Quick visit to local fish market","ACTIVITY","Cox's Bazar town",0,"See the fresh catch auction",3),
            act(d3,"10:00","20:00","Return Bus to Dhaka","Board return bus to Dhaka","TRAVEL","Cox's Bazar → Dhaka",900,"Book AC bus for comfort on return",4)));
        dayRepo.save(d3);

        // 2. Sylhet
        TravelPlanEntity p2 = planRepo.save(plan("bud-syl", "Sylhet Tea Trail",
            "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
            "BUDGET", 3, 2, 5000,
            "Explore lush tea gardens, crystal-clear rivers of Jaflong, and the enchanting Ratargul swamp forest. Stay in budget accommodations in Sylhet city.",
            "October – March", "1–4 persons", "Sylhet",
            "Tea Gardens", "Jaflong", "Ratargul Swamp", "Seven Layer Tea"));
        costRepo.saveAll(List.of(cb(p2,"TRANSPORT",1400,"Bus + local transport"), cb(p2,"ACCOMMODATION",1800,"Budget hotel 2 nights"), cb(p2,"FOOD",1100,"Local restaurants"), cb(p2,"ACTIVITIES",400,"Boat rides & entry fees"), cb(p2,"MISCELLANEOUS",300,"Misc")));
        ItineraryDayEntity p2d1 = dayRepo.save(day(p2,1,"Journey & Tea Gardens","Travel to Sylhet, visit Malnicherra tea garden."));
        p2d1.setActivities(List.of(
            act(p2d1,"06:00","06:30","Wake Up","Prepare for journey","REST","Dhaka",0,"",1),
            act(p2d1,"07:00","14:00","Bus to Sylhet","Travel from Dhaka to Sylhet by bus","TRAVEL","Dhaka → Sylhet",800,"Take Shyamoli or Green Line",2),
            act(p2d1,"14:30","15:30","Lunch & Check-in","Check into hotel, have lunch","FOOD","Sylhet city",300,"Hotels near Zindabazar are cheapest",3),
            act(p2d1,"16:00","18:00","Malnicherra Tea Estate","Visit Bangladesh's oldest tea garden","SIGHTSEEING","Malnicherra",0,"Free entry, great for photos",4),
            act(p2d1,"18:30","19:00","Seven Layer Tea","Try the famous 7-layer tea at Romesh Ram's stall","FOOD","Sylhet city",50,"A must-try Sylhet specialty",5),
            act(p2d1,"19:30","20:30","Dinner","Local Sylheti cuisine","FOOD","Sylhet city",200,"Try Sylheti pitha",6)));
        dayRepo.save(p2d1);
        ItineraryDayEntity p2d2 = dayRepo.save(day(p2,2,"Jaflong & Lalakhal","Full day exploring Jaflong river and Lalakhal blue waters."));
        p2d2.setActivities(List.of(
            act(p2d2,"07:00","08:00","Breakfast","Hotel breakfast","FOOD","Sylhet",100,"",1),
            act(p2d2,"08:30","10:30","Travel to Jaflong","Drive to Jaflong through scenic hills","TRAVEL","Sylhet → Jaflong",200,"Hire a shared CNG or Laguna",2),
            act(p2d2,"10:30","13:00","Jaflong","Explore crystal-clear Piyain River, see stone collectors, views of Meghalaya hills","SIGHTSEEING","Jaflong",50,"Don't swim in strong current areas",3),
            act(p2d2,"13:00","14:00","Lunch","Lunch at Jaflong stall","FOOD","Jaflong",150,"Try local fresh fish",4),
            act(p2d2,"14:30","16:30","Lalakhal","Boat ride on the stunning blue-green Lalakhal river","SIGHTSEEING","Lalakhal",150,"Boat ride is the highlight — negotiate price",5),
            act(p2d2,"17:00","18:30","Return to Sylhet","Drive back to Sylhet","TRAVEL","Lalakhal → Sylhet",200,"",6),
            act(p2d2,"19:30","20:30","Dinner","Dinner in Sylhet","FOOD","Sylhet",200,"",7)));
        dayRepo.save(p2d2);
        ItineraryDayEntity p2d3 = dayRepo.save(day(p2,3,"Ratargul & Return","Visit Ratargul swamp forest then return to Dhaka."));
        p2d3.setActivities(List.of(
            act(p2d3,"06:00","07:00","Early Start","Wake up early for Ratargul trip","REST","Sylhet",0,"",1),
            act(p2d3,"07:00","08:30","Travel to Ratargul","Auto-rickshaw to Ratargul swamp forest","TRAVEL","Sylhet → Ratargul",150,"",2),
            act(p2d3,"08:30","11:00","Ratargul Swamp Forest","Boat tour through Bangladesh's only freshwater swamp forest","SIGHTSEEING","Ratargul",100,"Go early for best experience. Bring mosquito repellent",3),
            act(p2d3,"11:30","12:30","Lunch & Checkout","Return, checkout, lunch","FOOD","Sylhet",200,"",4),
            act(p2d3,"13:00","20:00","Return to Dhaka","Bus back to Dhaka","TRAVEL","Sylhet → Dhaka",800,"",5)));
        dayRepo.save(p2d3);

        // 3. Khulna – Sundarbans
        TravelPlanEntity p3 = planRepo.save(plan("bud-khl", "Sundarbans Quick Tour",
            "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800",
            "BUDGET", 2, 1, 4000,
            "A quick budget trip into the world's largest mangrove forest. Spot wildlife, cruise through narrow creeks, and experience the raw beauty of the Sundarbans.",
            "November – February", "4–8 persons", "Khulna",
            "Mangrove Forest", "Wildlife Spotting", "Boat Cruise", "UNESCO Site"));
        costRepo.saveAll(List.of(cb(p3,"TRANSPORT",1200,"Bus to Khulna + shared boat"), cb(p3,"ACCOMMODATION",800,"Boat cabin overnight"), cb(p3,"FOOD",800,"Packed meals on boat"), cb(p3,"ACTIVITIES",900,"Forest entry + guide"), cb(p3,"MISCELLANEOUS",300,"Misc")));
        ItineraryDayEntity p3d1 = dayRepo.save(day(p3,1,"Journey to Sundarbans","Travel to Khulna, board boat, enter Sundarbans."));
        p3d1.setActivities(List.of(
            act(p3d1,"05:00","05:30","Early Departure","Leave Dhaka very early","REST","Dhaka",0,"",1),
            act(p3d1,"06:00","12:00","Bus to Khulna","Travel to Khulna by bus","TRAVEL","Dhaka → Khulna",700,"Take Hanif or Souhardo",2),
            act(p3d1,"12:30","13:30","Lunch in Khulna","Lunch at local restaurant","FOOD","Khulna",200,"",3),
            act(p3d1,"14:00","14:30","Board Boat","Board shared tourist boat at Mongla Ghat","TRAVEL","Mongla",500,"Join a group for cheaper rates",4),
            act(p3d1,"14:30","17:00","Enter Sundarbans","Cruise through mangrove channels, spot wildlife","SIGHTSEEING","Sundarbans",400,"Look for deer, monkeys, crocodiles",5),
            act(p3d1,"17:30","18:30","Sunset on Boat","Watch sunset from the boat deck","SIGHTSEEING","Sundarbans",0,"Magical experience",6),
            act(p3d1,"19:00","20:00","Dinner on Boat","Dinner on the boat","FOOD","Sundarbans",200,"Fresh river fish",7)));
        dayRepo.save(p3d1);
        ItineraryDayEntity p3d2 = dayRepo.save(day(p3,2,"Forest Walk & Return","Morning forest trek, then return to Dhaka."));
        p3d2.setActivities(List.of(
            act(p3d2,"06:00","07:00","Sunrise","Watch sunrise from boat deck","SIGHTSEEING","Sundarbans",0,"",1),
            act(p3d2,"07:30","08:30","Breakfast","Breakfast on boat","FOOD","Sundarbans",150,"",2),
            act(p3d2,"09:00","11:00","Forest Trail Walk","Guided walk through mangrove trails","ACTIVITY","Sundarbans",200,"Stay with guide, don't wander",3),
            act(p3d2,"11:30","13:00","Return Cruise","Boat back to Mongla","TRAVEL","Sundarbans → Mongla",0,"",4),
            act(p3d2,"13:30","14:30","Lunch","Lunch in Khulna","FOOD","Khulna",200,"",5),
            act(p3d2,"15:00","22:00","Return to Dhaka","Bus back to Dhaka","TRAVEL","Khulna → Dhaka",700,"Night bus option available",6)));
        dayRepo.save(p3d2);

        // 4. Dhaka
        TravelPlanEntity p4 = planRepo.save(plan("bud-dhk", "Old Dhaka Heritage Walk",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "BUDGET", 2, 1, 3000,
            "Explore the historic heart of Dhaka — Mughal forts, riverside palaces, ornate mosques, and the vibrant chaos of Old Dhaka's streets and food scene.",
            "October – March", "1–4 persons", "Dhaka",
            "Lalbagh Fort", "Ahsan Manzil", "Star Mosque", "Old Dhaka Food"));
        costRepo.saveAll(List.of(cb(p4,"TRANSPORT",400,"Rickshaw & CNG"), cb(p4,"ACCOMMODATION",1200,"Budget hotel"), cb(p4,"FOOD",900,"Street food & restaurants"), cb(p4,"ACTIVITIES",300,"Entry fees"), cb(p4,"MISCELLANEOUS",200,"Misc")));
        ItineraryDayEntity p4d1 = dayRepo.save(day(p4,1,"Old Dhaka Heritage","Full day exploring Old Dhaka's Mughal heritage."));
        p4d1.setActivities(List.of(
            act(p4d1,"08:00","09:00","Breakfast","Traditional Dhaka breakfast — puri & halwa","FOOD","Old Dhaka",80,"Try Al-Razzaq in Bangshal",1),
            act(p4d1,"09:30","11:30","Lalbagh Fort","Explore the 17th-century Mughal fort, Pari Bibi tomb","SIGHTSEEING","Lalbagh",20,"Entry ₹20. Closed Sunday",2),
            act(p4d1,"12:00","13:00","Ahsan Manzil","Visit the iconic Pink Palace museum","SIGHTSEEING","Kumartuli",20,"Great river views from balcony",3),
            act(p4d1,"13:30","14:30","Lunch","Biryani at Haji Biryani","FOOD","Old Dhaka",150,"Famous since 1939",4),
            act(p4d1,"15:00","16:00","Star Mosque","Visit the stunning mosaic Star Mosque","SIGHTSEEING","Armanitola",0,"Beautiful tile work",5),
            act(p4d1,"16:30","17:30","Armenian Church","Visit the 18th-century Armenian Church","SIGHTSEEING","Armanitola",0,"One of the oldest churches in Dhaka",6),
            act(p4d1,"18:00","19:00","Sadarghat River","Watch sunset at Sadarghat river terminal","SIGHTSEEING","Sadarghat",0,"Incredible boat activity at sunset",7),
            act(p4d1,"19:30","21:00","Street Food Tour","Explore Old Dhaka street food — bakarkhani, falooda, kebabs","FOOD","Chawk Bazar",300,"Must-try: Nanna Biryani, Mama Falooda",8)));
        dayRepo.save(p4d1);
        ItineraryDayEntity p4d2 = dayRepo.save(day(p4,2,"Modern Dhaka & Museums","Visit national landmarks and museums."));
        p4d2.setActivities(List.of(
            act(p4d2,"08:00","09:00","Breakfast","Hotel breakfast","FOOD","Dhaka",100,"",1),
            act(p4d2,"09:30","11:00","National Museum","Explore Bangladesh's largest museum","SIGHTSEEING","Shahbagh",20,"",2),
            act(p4d2,"11:30","12:30","Liberation War Museum","Moving museum about the 1971 war","SIGHTSEEING","Segunbagicha",50,"Allow 1+ hours",3),
            act(p4d2,"13:00","14:00","Lunch","Lunch at Shahbagh restaurant","FOOD","Shahbagh",200,"",4),
            act(p4d2,"14:30","16:00","Parliament House Area","See Louis Kahn's masterpiece from outside, walk Crescent Lake","SIGHTSEEING","Sher-e-Bangla Nagar",0,"",5),
            act(p4d2,"16:30","17:30","Shaheed Minar","Visit the iconic Language Movement monument","SIGHTSEEING","DU campus",0,"",6)));
        dayRepo.save(p4d2);

        // 5. Rajshahi
        TravelPlanEntity p5 = planRepo.save(plan("bud-raj", "Rajshahi Heritage Trail",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "BUDGET", 2, 1, 3500,
            "Discover the ancient heritage of North Bengal — from the UNESCO Paharpur monastery to the Varendra Research Museum and the mighty Padma River.",
            "October – March", "1–4 persons", "Rajshahi",
            "Paharpur UNESCO", "Varendra Museum", "Padma River", "Mango Country"));
        costRepo.saveAll(List.of(cb(p5,"TRANSPORT",1200,"Bus + local"), cb(p5,"ACCOMMODATION",1000,"Budget hotel"), cb(p5,"FOOD",800,"Local food"), cb(p5,"ACTIVITIES",300,"Entry fees"), cb(p5,"MISCELLANEOUS",200,"Misc")));
        ItineraryDayEntity p5d1 = dayRepo.save(day(p5,1,"Journey & Rajshahi City","Travel to Rajshahi, explore the city and Padma riverside."));
        p5d1.setActivities(List.of(
            act(p5d1,"06:00","12:00","Bus to Rajshahi","Travel from Dhaka","TRAVEL","Dhaka → Rajshahi",700,"Take BRTC or Hanif",1),
            act(p5d1,"12:30","13:30","Lunch","Lunch in Rajshahi","FOOD","Rajshahi",200,"",2),
            act(p5d1,"14:00","14:30","Check-in","Hotel check-in","REST","Rajshahi",500,"Near Saheb Bazar",3),
            act(p5d1,"15:00","16:30","Varendra Museum","Visit Bangladesh's oldest museum","SIGHTSEEING","Rajshahi",20,"Excellent archaeological collection",4),
            act(p5d1,"17:00","18:30","Padma Riverside","Walk along the Padma River embankment at sunset","SIGHTSEEING","Padma River",0,"Beautiful sunset views",5),
            act(p5d1,"19:00","20:00","Dinner","Dinner at local restaurant","FOOD","Rajshahi",200,"Try Rajshahi's famous Kalai roti",6)));
        dayRepo.save(p5d1);
        ItineraryDayEntity p5d2 = dayRepo.save(day(p5,2,"Paharpur & Return","Visit UNESCO Paharpur site then return to Dhaka."));
        p5d2.setActivities(List.of(
            act(p5d2,"06:30","07:30","Breakfast & Checkout","Hotel breakfast","FOOD","Rajshahi",100,"",1),
            act(p5d2,"08:00","10:00","Travel to Paharpur","Drive to Paharpur archaeological site","TRAVEL","Rajshahi → Paharpur",200,"Hire a local van",2),
            act(p5d2,"10:00","12:00","Paharpur Vihara","Explore the UNESCO 8th-century Buddhist monastery ruins","SIGHTSEEING","Paharpur",100,"Second largest Buddhist monastery south of Himalayas",3),
            act(p5d2,"12:30","13:30","Lunch","Lunch en route","FOOD","Naogaon",150,"",4),
            act(p5d2,"14:00","21:00","Return to Dhaka","Travel back to Dhaka","TRAVEL","Rajshahi → Dhaka",700,"",5)));
        dayRepo.save(p5d2);

        // 6. Rangpur
        TravelPlanEntity p6 = planRepo.save(plan("bud-rng", "Rangpur Quick Getaway",
            "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=800",
            "BUDGET", 2, 1, 3200,
            "Visit the stunning Kantajew Temple, the Tajhat Palace, and explore the charming heritage of Bangladesh's northern region.",
            "October – March", "1–4 persons", "Rangpur",
            "Kantajew Temple", "Tajhat Palace", "Northern Heritage", "Rangpur Town"));
        costRepo.saveAll(List.of(cb(p6,"TRANSPORT",1200,"Bus + local"), cb(p6,"ACCOMMODATION",900,"Budget hotel"), cb(p6,"FOOD",700,"Local food"), cb(p6,"ACTIVITIES",200,"Entry fees"), cb(p6,"MISCELLANEOUS",200,"Misc")));
        ItineraryDayEntity p6d1 = dayRepo.save(day(p6,1,"Journey & Kantajew Temple","Travel to Rangpur region, visit Kantajew Temple."));
        p6d1.setActivities(List.of(
            act(p6d1,"06:00","13:00","Bus to Dinajpur","Travel from Dhaka to Dinajpur","TRAVEL","Dhaka → Dinajpur",800,"Take Hanif or BRTC",1),
            act(p6d1,"13:30","14:30","Lunch","Local lunch in Dinajpur","FOOD","Dinajpur",150,"",2),
            act(p6d1,"15:00","17:00","Kantajew Temple","Visit the magnificent terracotta Hindu temple","SIGHTSEEING","Dinajpur",20,"One of the finest terracotta temples in Bangladesh",3),
            act(p6d1,"17:30","18:30","Nayabad Mosque","Visit the historic mosque nearby","SIGHTSEEING","Dinajpur",0,"",4),
            act(p6d1,"19:00","20:00","Dinner","Dinner in Dinajpur","FOOD","Dinajpur",200,"",5),
            act(p6d1,"20:30","21:00","Check-in","Hotel check-in","REST","Dinajpur",500,"",6)));
        dayRepo.save(p6d1);
        ItineraryDayEntity p6d2 = dayRepo.save(day(p6,2,"Tajhat Palace & Return","Visit Tajhat Palace in Rangpur then return."));
        p6d2.setActivities(List.of(
            act(p6d2,"07:00","08:00","Breakfast & Checkout","","FOOD","Dinajpur",100,"",1),
            act(p6d2,"08:30","10:00","Travel to Rangpur","Bus to Rangpur city","TRAVEL","Dinajpur → Rangpur",150,"",2),
            act(p6d2,"10:30","12:00","Tajhat Palace","Visit the grand Tajhat Rajbari palace and museum","SIGHTSEEING","Rangpur",20,"Beautiful Mughal-style architecture",3),
            act(p6d2,"12:30","13:30","Lunch","Lunch in Rangpur","FOOD","Rangpur",200,"",4),
            act(p6d2,"14:00","22:00","Return to Dhaka","Bus back to Dhaka","TRAVEL","Rangpur → Dhaka",800,"Night journey",5)));
        dayRepo.save(p6d2);

        // 7. Barishal
        TravelPlanEntity p7 = planRepo.save(plan("bud-bar", "Barishal River Town",
            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
            "BUDGET", 2, 1, 3500,
            "Experience the Venice of the East — cruise through floating markets, explore colonial-era architecture, and enjoy the peaceful riverine landscape of southern Bangladesh.",
            "October – March", "1–4 persons", "Barishal",
            "Floating Market", "River Cruise", "Colonial Heritage", "Launch Journey"));
        costRepo.saveAll(List.of(cb(p7,"TRANSPORT",1000,"Launch + local"), cb(p7,"ACCOMMODATION",1000,"Budget hotel"), cb(p7,"FOOD",900,"Local food"), cb(p7,"ACTIVITIES",300,"Boat rides"), cb(p7,"MISCELLANEOUS",300,"Misc")));
        ItineraryDayEntity p7d1 = dayRepo.save(day(p7,1,"Launch Journey & Arrival","Overnight launch from Dhaka, arrive Barishal morning."));
        p7d1.setActivities(List.of(
            act(p7d1,"20:00","21:00","Board Launch","Board overnight launch from Sadarghat","TRAVEL","Sadarghat, Dhaka",500,"Deck class is cheapest. Bring a blanket",1),
            act(p7d1,"21:00","06:00","Overnight Journey","Sleep on the launch through beautiful river channels","REST","River",0,"Wake up for sunrise on the river",2)));
        dayRepo.save(p7d1);
        ItineraryDayEntity p7d2 = dayRepo.save(day(p7,2,"Barishal Exploration & Return","Explore floating market, town sights, then return."));
        p7d2.setActivities(List.of(
            act(p7d2,"06:00","06:30","Arrival","Arrive at Barishal launch terminal","TRAVEL","Barishal",0,"",1),
            act(p7d2,"07:00","08:00","Breakfast","Local breakfast","FOOD","Barishal",100,"",2),
            act(p7d2,"08:30","10:30","Floating Market","Visit the famous floating guava market (seasonal)","SIGHTSEEING","Swarupkathi",200,"Best July–August for guava season. Boat required",3),
            act(p7d2,"11:00","12:30","Town Heritage Walk","Explore Barishal's colonial buildings and Durga Sagar pond","SIGHTSEEING","Barishal town",0,"Largest pond in southern Bangladesh",4),
            act(p7d2,"13:00","14:00","Lunch","Local river fish lunch","FOOD","Barishal",200,"Try Hilsa if in season",5),
            act(p7d2,"15:00","16:00","River Cruise","Short boat ride on Kirtankhola River","ACTIVITY","Barishal",100,"Beautiful views",6),
            act(p7d2,"20:00","06:00","Return Launch","Overnight launch back to Dhaka","TRAVEL","Barishal → Dhaka",500,"Arrive Dhaka early morning",7)));
        dayRepo.save(p7d2);

        // 8. Mymensingh
        TravelPlanEntity p8 = planRepo.save(plan("bud-mym", "Mymensingh Heritage Walk",
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
            "BUDGET", 2, 1, 3000,
            "Discover the zamindari heritage of Mymensingh — stunning palaces, the Brahmaputra riverside, and the lush botanical garden of Bangladesh Agricultural University.",
            "October – March", "1–4 persons", "Mymensingh",
            "Shashi Lodge", "Alexander Castle", "BAU Garden", "Brahmaputra River"));
        costRepo.saveAll(List.of(cb(p8,"TRANSPORT",600,"Bus + local"), cb(p8,"ACCOMMODATION",1000,"Budget hotel"), cb(p8,"FOOD",900,"Local food"), cb(p8,"ACTIVITIES",200,"Entry fees"), cb(p8,"MISCELLANEOUS",300,"Misc")));
        ItineraryDayEntity p8d1 = dayRepo.save(day(p8,1,"Journey & Palace Tour","Travel to Mymensingh, explore palaces and riverside."));
        p8d1.setActivities(List.of(
            act(p8d1,"07:00","09:30","Bus to Mymensingh","Travel from Dhaka","TRAVEL","Dhaka → Mymensingh",250,"Only 2-3 hours by bus",1),
            act(p8d1,"10:00","10:30","Check-in","Hotel near town center","REST","Mymensingh",500,"",2),
            act(p8d1,"11:00","12:30","Shashi Lodge","Visit the beautiful Greek-revival zamindari palace","SIGHTSEEING","Mymensingh",20,"Stunning gardens",3),
            act(p8d1,"12:30","13:30","Lunch","Local lunch","FOOD","Mymensingh",200,"",4),
            act(p8d1,"14:00","15:30","Alexander Castle","Explore the majestic 1905 castle","SIGHTSEEING","Mymensingh",0,"Now a teachers' training college",5),
            act(p8d1,"16:00","17:30","Brahmaputra Embankment","Evening walk along the Old Brahmaputra River","SIGHTSEEING","Mymensingh",0,"Great sunset spot",6),
            act(p8d1,"18:00","19:30","Dinner","Dinner at local restaurant","FOOD","Mymensingh",200,"Try local curd (doi)",7)));
        dayRepo.save(p8d1);
        ItineraryDayEntity p8d2 = dayRepo.save(day(p8,2,"BAU Garden & Return","Visit BAU Botanical Garden then return to Dhaka."));
        p8d2.setActivities(List.of(
            act(p8d2,"07:30","08:30","Breakfast & Checkout","","FOOD","Mymensingh",100,"",1),
            act(p8d2,"09:00","11:00","BAU Botanical Garden","Explore Bangladesh's largest botanical garden","SIGHTSEEING","BAU campus",0,"Beautiful walking trails",2),
            act(p8d2,"11:30","12:30","Muktagachha Monda","Visit Muktagachha for the famous Monda sweet","ACTIVITY","Muktagachha",100,"A legendary Bengali sweet, must try",3),
            act(p8d2,"13:00","14:00","Lunch","Lunch in Mymensingh","FOOD","Mymensingh",200,"",4),
            act(p8d2,"14:30","17:00","Return to Dhaka","Bus back to Dhaka","TRAVEL","Mymensingh → Dhaka",250,"Short journey",5)));
        dayRepo.save(p8d2);
    }

    // ════════════════════════════════════════════
    //  STANDARD TIER (8 plans)
    // ════════════════════════════════════════════
    private void seedStandardPlans() {
        // 1. Chattogram
        TravelPlanEntity p = planRepo.save(plan("std-ctg", "Cox's Bazar Comfort",
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
            "STANDARD", 4, 3, 15000,
            "A comfortable Cox's Bazar experience with 3-star hotel, AC transport, guided tours to Himchari, Inani, and a day trip to Teknaf with sea views.",
            "October – March", "2–6 persons", "Chattogram",
            "3-Star Hotel", "AC Transport", "Teknaf Point", "Guided Tours"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3500,"AC bus + private CNG"), cb(p,"ACCOMMODATION",6000,"3-star hotel 3 nights"), cb(p,"FOOD",3000,"Mid-range restaurants"), cb(p,"ACTIVITIES",1500,"Tours & entries"), cb(p,"MISCELLANEOUS",1000,"Misc")));
        seedStdDays(p, "Cox's Bazar beach walk & hotel relaxation", "Himchari, Inani Beach & Marine Drive full day", "Teknaf zero point excursion & Burmese Market", "Morning swim, checkout & return");

        // 2. Sylhet
        p = planRepo.save(plan("std-syl", "Sylhet Nature Explorer",
            "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
            "STANDARD", 4, 3, 14000,
            "Comprehensive Sylhet exploration with comfortable stays. Visit Ratargul, Jaflong, Lalakhal, Bichnakandi, and Srimangal's tea estates.",
            "October – March", "2–6 persons", "Sylhet",
            "Ratargul", "Bichnakandi", "Srimangal", "Comfortable Stay"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3000,"AC bus + local hire"), cb(p,"ACCOMMODATION",5500,"3-star hotel 3 nights"), cb(p,"FOOD",3000,"Restaurants"), cb(p,"ACTIVITIES",1500,"Boat rides & entries"), cb(p,"MISCELLANEOUS",1000,"Misc")));
        seedStdDays(p, "Travel & Malnicherra Tea Estate", "Jaflong & Lalakhal full day tour", "Ratargul & Bichnakandi exploration", "Srimangal day trip & return");

        // 3. Khulna
        p = planRepo.save(plan("std-khl", "Sundarbans Adventure",
            "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800",
            "STANDARD", 3, 2, 13000,
            "A proper Sundarbans adventure with private boat cabin, experienced guide, multiple forest trails, and wildlife watching towers.",
            "November – February", "4–8 persons", "Khulna",
            "Private Cabin", "Wildlife Towers", "Multiple Trails", "Professional Guide"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3000,"AC bus + private boat"), cb(p,"ACCOMMODATION",4000,"Boat cabin 2 nights"), cb(p,"FOOD",2500,"Catered meals"), cb(p,"ACTIVITIES",2500,"Guide + forest fees"), cb(p,"MISCELLANEOUS",1000,"Misc")));
        seedStdDays(p, "Travel to Mongla & board boat, enter Sundarbans", "Full day forest exploration — Karamjal, Katka, wildlife towers", "Dawn safari, Dublar Char & return to Dhaka");

        // 4. Dhaka
        p = planRepo.save(plan("std-dhk", "Dhaka City Explorer",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "STANDARD", 3, 2, 10000,
            "Complete Dhaka experience with comfortable hotel, AC transport, and curated heritage tours covering both old and modern Dhaka with food tasting.",
            "October – March", "2–4 persons", "Dhaka",
            "Guided Heritage Tour", "Food Tasting", "AC Transport", "3-Star Hotel"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",1500,"AC transport & Uber"), cb(p,"ACCOMMODATION",4500,"3-star hotel 2 nights"), cb(p,"FOOD",2500,"Curated food tours"), cb(p,"ACTIVITIES",1000,"Guide + entries"), cb(p,"MISCELLANEOUS",500,"Misc")));
        seedStdDays(p, "Old Dhaka heritage — Lalbagh, Ahsan Manzil, Star Mosque, food tour", "Modern Dhaka — Parliament, museums, Hatirjheel, shopping", "Sonargaon day trip & departure");

        // 5. Rajshahi
        p = planRepo.save(plan("std-raj", "Rajshahi & Paharpur",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "STANDARD", 3, 2, 11000,
            "Explore Rajshahi's rich archaeological heritage with comfortable AC travel, including Paharpur UNESCO site, Puthia Temple Complex, and Bagha Mosque.",
            "October – March", "2–4 persons", "Rajshahi",
            "UNESCO Paharpur", "Puthia Temples", "Bagha Mosque", "AC Transport"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3000,"AC bus + local hire"), cb(p,"ACCOMMODATION",3500,"3-star hotel 2 nights"), cb(p,"FOOD",2500,"Restaurants"), cb(p,"ACTIVITIES",1500,"Entries + guide"), cb(p,"MISCELLANEOUS",500,"Misc")));
        seedStdDays(p, "Travel to Rajshahi, Puthia Temple Complex, Padma sunset", "Paharpur UNESCO site full day & Bagha Mosque", "Varendra Museum, silk shopping & return");

        // 6. Rangpur
        p = planRepo.save(plan("std-rng", "Rangpur-Dinajpur Explorer",
            "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=800",
            "STANDARD", 3, 2, 10500,
            "Comprehensive northern Bangladesh tour covering Kantajew Temple, Ramsagar Lake, Tajhat Palace, and the unique Dinajpur heritage sites.",
            "October – March", "2–4 persons", "Rangpur",
            "Kantajew Temple", "Ramsagar National Park", "Tajhat Palace", "Comfortable Stay"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3000,"AC bus + local"), cb(p,"ACCOMMODATION",3500,"3-star hotel 2 nights"), cb(p,"FOOD",2000,"Restaurants"), cb(p,"ACTIVITIES",1500,"Entries"), cb(p,"MISCELLANEOUS",500,"Misc")));
        seedStdDays(p, "Travel to Dinajpur, Kantajew Temple, Nayabad Mosque", "Ramsagar Lake, Dinajpur Museum & travel to Rangpur", "Tajhat Palace, Rangpur heritage & return");

        // 7. Barishal
        p = planRepo.save(plan("std-bar", "Barishal-Kuakata Coastal",
            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
            "STANDARD", 3, 2, 12000,
            "Combine the river beauty of Barishal with Kuakata's unique sunrise-sunset beach. Comfortable hotel stays and AC transport throughout.",
            "October – March", "2–4 persons", "Barishal",
            "Kuakata Beach", "Floating Market", "Sunrise & Sunset", "River Journey"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",3500,"Launch + AC bus"), cb(p,"ACCOMMODATION",4000,"Hotels 2 nights"), cb(p,"FOOD",2500,"Restaurants"), cb(p,"ACTIVITIES",1500,"Tours"), cb(p,"MISCELLANEOUS",500,"Misc")));
        seedStdDays(p, "Launch to Barishal, floating market, river cruise", "Travel to Kuakata, sunset at the beach, night stays", "Kuakata sunrise, Buddhist temple, return to Dhaka");

        // 8. Mymensingh
        p = planRepo.save(plan("std-mym", "Mymensingh Nature Trail",
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
            "STANDARD", 3, 2, 10000,
            "Explore Mymensingh's zamindari grandeur, BAU campus, Muktagachha palace, Madhupur forest, and enjoy the charm of this historic river town.",
            "October – March", "2–4 persons", "Mymensingh",
            "Palace Tour", "Madhupur Forest", "BAU Campus", "Muktagachha"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",2000,"AC bus + local"), cb(p,"ACCOMMODATION",4000,"Hotel 2 nights"), cb(p,"FOOD",2500,"Restaurants"), cb(p,"ACTIVITIES",1000,"Entries"), cb(p,"MISCELLANEOUS",500,"Misc")));
        seedStdDays(p, "Travel, Shashi Lodge, Alexander Castle, Brahmaputra sunset", "Madhupur National Park day trip, tribal village visit", "BAU Garden, Muktagachha Palace, Monda tasting & return");
    }

    /** Quick helper to seed standard-tier days with summary activities */
    private void seedStdDays(TravelPlanEntity p, String... daySummaries) {
        for (int i = 0; i < daySummaries.length; i++) {
            String title = "Day " + (i + 1);
            ItineraryDayEntity d = dayRepo.save(day(p, i + 1, title, daySummaries[i]));
            // Add summary activities
            String[] parts = daySummaries[i].split(",|&|—");
            int order = 1;
            for (String part : parts) {
                String activity = part.trim();
                if (!activity.isEmpty()) {
                    String type = activity.toLowerCase().contains("travel") || activity.toLowerCase().contains("return") ? "TRAVEL" :
                                  activity.toLowerCase().contains("food") || activity.toLowerCase().contains("lunch") || activity.toLowerCase().contains("dinner") || activity.toLowerCase().contains("breakfast") || activity.toLowerCase().contains("tasting") ? "FOOD" :
                                  "SIGHTSEEING";
                    String startH = String.format("%02d:00", 7 + (order - 1) * 2);
                    String endH = String.format("%02d:00", 9 + (order - 1) * 2);
                    d.getActivities().add(act(d, startH, endH, activity, activity, type, p.getDestination(), 0.0, "", order));
                    order++;
                }
            }
            dayRepo.save(d);
        }
    }

    // ════════════════════════════════════════════
    //  PREMIUM TIER (8 plans)
    // ════════════════════════════════════════════
    private void seedPremiumPlans() {
        // 1. Chattogram
        TravelPlanEntity p = planRepo.save(plan("prm-ctg", "Cox's Bazar & St. Martin Luxury",
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
            "PREMIUM", 5, 4, 35000,
            "The ultimate Cox's Bazar experience — 4-star beachfront resort, private car, St. Martin Island excursion with resort stay, spa treatments, and curated dining.",
            "November – February", "2–4 persons", "Chattogram",
            "4-Star Resort", "St. Martin Island", "Spa & Wellness", "Private Transport"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",7000,"Flight + private car"), cb(p,"ACCOMMODATION",15000,"4-star resort 4 nights"), cb(p,"FOOD",6000,"Fine dining"), cb(p,"ACTIVITIES",5000,"St. Martin + spa"), cb(p,"MISCELLANEOUS",2000,"Misc")));
        seedStdDays(p, "Flight to Cox's Bazar, resort check-in, beach sunset", "Private car to Himchari & Inani, spa afternoon", "Ship to St. Martin Island, coral beach, snorkeling", "St. Martin sunrise, Chera Dwip island, return to Cox's Bazar", "Morning swim, resort checkout, flight to Dhaka");

        // 2. Sylhet
        p = planRepo.save(plan("prm-syl", "Sylhet Luxury Retreat",
            "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
            "PREMIUM", 5, 4, 30000,
            "Luxury tea country retreat with boutique resort stays, private guided tours to all major attractions, and exclusive Srimangal tea tasting experiences.",
            "October – March", "2–4 persons", "Sylhet",
            "Boutique Resort", "Private Guides", "Tea Tasting", "All-Inclusive"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",6000,"Flight + private car"), cb(p,"ACCOMMODATION",13000,"Boutique resorts"), cb(p,"FOOD",5000,"Curated dining"), cb(p,"ACTIVITIES",4000,"Private tours"), cb(p,"MISCELLANEOUS",2000,"Misc")));
        seedStdDays(p, "Flight to Sylhet, resort check-in, tea garden sunset walk", "Private tour to Jaflong & Lalakhal with packed gourmet lunch", "Ratargul swamp & Bichnakandi full-day private tour", "Srimangal – Lawachara forest & premium tea tasting", "Madhabpur Lake, shopping & flight home");

        // 3. Khulna
        p = planRepo.save(plan("prm-khl", "Sundarbans Premium Safari",
            "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800",
            "PREMIUM", 4, 3, 28000,
            "Premium Sundarbans expedition with luxury houseboat, personal naturalist guide, multiple wildlife trails, watchtowers, and gourmet catering.",
            "November – February", "2–6 persons", "Khulna",
            "Luxury Houseboat", "Naturalist Guide", "Multi-Trail", "Gourmet Catering"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",6000,"Flight to Jessore + private car"), cb(p,"ACCOMMODATION",10000,"Luxury houseboat 3 nights"), cb(p,"FOOD",5000,"Gourmet meals"), cb(p,"ACTIVITIES",5000,"Guide + premium permits"), cb(p,"MISCELLANEOUS",2000,"Misc")));
        seedStdDays(p, "Flight to Jessore, private transfer to Mongla, board luxury houseboat", "Karamjal wildlife center, Harbaria trail, watchtower sunrise", "Katka tiger territory, Kochikhali beach, night wildlife sounds", "Dawn birdwatching, return cruise, flight to Dhaka");

        // 4. Dhaka
        p = planRepo.save(plan("prm-dhk", "Dhaka Royal Heritage",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "PREMIUM", 3, 2, 22000,
            "Dhaka like royalty — 5-star hotel, private guided heritage tours, exclusive food trails with celebrity chefs, and a premium Sonargaon day trip.",
            "October – March", "2–4 persons", "Dhaka",
            "5-Star Hotel", "Celebrity Chef Dinner", "Private Guide", "Sonargaon Excursion"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",2000,"Private car"), cb(p,"ACCOMMODATION",12000,"5-star hotel"), cb(p,"FOOD",4500,"Fine dining & food trails"), cb(p,"ACTIVITIES",2500,"Private tours"), cb(p,"MISCELLANEOUS",1000,"Misc")));
        seedStdDays(p, "5-star check-in, private Old Dhaka heritage tour with historian guide", "Sonargaon Day trip – Panam City & Folk Art Museum, dinner experience", "Parliament House, Hatirjheel cruise, hotel spa & checkout");

        // 5. Rajshahi
        p = planRepo.save(plan("prm-raj", "North Bengal Premium",
            "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800",
            "PREMIUM", 4, 3, 25000,
            "Luxury archaeological tour of North Bengal covering Paharpur, Mahasthangarh, Puthia, with 4-star stays, AC car, and expert archaeologist guide.",
            "October – March", "2–4 persons", "Rajshahi",
            "Archaeologist Guide", "4-Star Hotel", "Mahasthangarh", "AC Car Throughout"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",6000,"AC car rental"), cb(p,"ACCOMMODATION",9000,"4-star hotel 3 nights"), cb(p,"FOOD",5000,"Fine dining"), cb(p,"ACTIVITIES",3500,"Expert guide + entries"), cb(p,"MISCELLANEOUS",1500,"Misc")));
        seedStdDays(p, "AC car to Rajshahi, 4-star check-in, Puthia Temples sunset", "Paharpur UNESCO full day with archaeologist guide", "Mahasthangarh ancient city & Bogra historical sites", "Silk factory visit, Padma sunset cruise & return");

        // 6. Rangpur
        p = planRepo.save(plan("prm-rng", "North Bengal Heritage Luxury",
            "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=800",
            "PREMIUM", 4, 3, 24000,
            "Luxury tour of northern Bangladesh's finest heritage sites — Kantajew Temple, Ramsagar, Tajhat Palace — with private car, premium hotel, and historian guide.",
            "October – March", "2–4 persons", "Rangpur",
            "Private Car", "4-Star Hotel", "Historian Guide", "Full Coverage"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",6000,"Private AC car"), cb(p,"ACCOMMODATION",9000,"Premium hotel 3 nights"), cb(p,"FOOD",4500,"Fine dining"), cb(p,"ACTIVITIES",3000,"Guide + entries"), cb(p,"MISCELLANEOUS",1500,"Misc")));
        seedStdDays(p, "Private car to Dinajpur, premium hotel, evening heritage walk", "Kantajew Temple, Ramsagar Lake picnic, Nayabad Mosque", "Drive to Rangpur, Tajhat Palace, Carmichael College", "Nilphamari weaving village visit & luxury return");

        // 7. Barishal
        p = planRepo.save(plan("prm-bar", "Southern Coast Premium",
            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
            "PREMIUM", 4, 3, 26000,
            "Premium southern Bangladesh experience — luxury launch cabin, Kuakata beachfront resort, sunrise & sunset from the same beach, private boat tours.",
            "October – March", "2–4 persons", "Barishal",
            "Luxury Launch", "Beachfront Resort", "Private Boat", "Sunrise & Sunset"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",5000,"Luxury launch + private car"), cb(p,"ACCOMMODATION",11000,"Premium stays 3 nights"), cb(p,"FOOD",5000,"Fine dining"), cb(p,"ACTIVITIES",3500,"Private tours"), cb(p,"MISCELLANEOUS",1500,"Misc")));
        seedStdDays(p, "VIP launch cabin to Barishal, floating market, premium dinner", "Private car to Kuakata, beachfront resort, sunset viewing", "Kuakata sunrise, Fatrar Char island by private boat, Buddhist temple", "Morning beach yoga, checkout, luxury launch return to Dhaka");

        // 8. Mymensingh
        p = planRepo.save(plan("prm-mym", "Mymensingh Palace Retreat",
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
            "PREMIUM", 3, 2, 20000,
            "Luxury heritage retreat exploring all major Mymensingh palaces, Madhupur forest, and agricultural university campus with private transport and premium dining.",
            "October – March", "2–4 persons", "Mymensingh",
            "Private Car", "Premium Hotel", "Full Palace Tour", "Madhupur Forest"));
        costRepo.saveAll(List.of(cb(p,"TRANSPORT",4000,"Private AC car"), cb(p,"ACCOMMODATION",8000,"Premium hotel 2 nights"), cb(p,"FOOD",4000,"Fine dining"), cb(p,"ACTIVITIES",2500,"Private tours"), cb(p,"MISCELLANEOUS",1500,"Misc")));
        seedStdDays(p, "Private car to Mymensingh, Shashi Lodge, Alexander Castle, premium dinner", "Madhupur forest, Garo tribal village, nature photography session", "Muktagachha Palace, Gauripur Palace ruins, BAU Garden & return");
    }
}
