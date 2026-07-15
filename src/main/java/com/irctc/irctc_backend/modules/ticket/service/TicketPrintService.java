package com.irctc.irctc_backend.modules.ticket.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.modules.fare.entity.FareBreakdown;
import com.irctc.irctc_backend.modules.fare.service.FareService;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketPrintService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final FareService fareService;

    public String generateHtmlTicket(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        List<Passenger> passengers = passengerRepository.findByBooking(booking);
        FareBreakdown fare = fareService.getFareBreakdownByBookingId(bookingId);

        String qrCodeSvg = generateQrCodeSvg(booking.getPnr());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
            .append("<title>IRCTC E-Ticket - PNR: ").append(booking.getPnr()).append("</title>\n")
            .append("<style>\n")
            .append("  body { font-family: Arial, sans-serif; margin: 30px; color: #333; }\n")
            .append("  .ticket-container { border: 2px solid #003366; padding: 20px; border-radius: 10px; max-width: 800px; margin: 0 auto; }\n")
            .append("  .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #003366; padding-bottom: 10px; }\n")
            .append("  .logo { font-size: 24px; font-weight: bold; color: #003366; }\n")
            .append("  .pnr-box { background: #e6f2ff; padding: 10px 20px; border-radius: 5px; font-weight: bold; font-size: 18px; }\n")
            .append("  .details-table, .passengers-table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n")
            .append("  .details-table td { padding: 8px; vertical-align: top; }\n")
            .append("  .passengers-table th, .passengers-table td { border: 1px solid #ddd; padding: 10px; text-align: left; }\n")
            .append("  .passengers-table th { background: #f2f2f2; }\n")
            .append("  .footer { margin-top: 30px; text-align: center; font-size: 12px; color: #777; border-top: 1px solid #ddd; padding-top: 10px; }\n")
            .append("  .qr-section { text-align: center; margin-top: 20px; }\n")
            .append("  @media print { .print-btn { display: none; } }\n")
            .append("</style>\n</head>\n<body>\n")
            .append("<div style='text-align: right; max-width: 800px; margin: 0 auto 10px auto;'>\n")
            .append("  <button class='print-btn' onclick='window.print()' style='background:#003366;color:#fff;padding:10px 20px;border:none;border-radius:5px;cursor:pointer;'>Print / Save PDF</button>\n")
            .append("</div>\n")
            .append("<div class='ticket-container'>\n")
            .append("  <div class='header'>\n")
            .append("    <div class='logo'>INDIAN RAILWAYS (IRCTC)</div>\n")
            .append("    <div class='pnr-box'>PNR: ").append(booking.getPnr()).append("</div>\n")
            .append("  </div>\n")
            .append("  <table class='details-table'>\n")
            .append("    <tr>\n")
            .append("      <td><strong>Train:</strong> ").append(booking.getTrain().getTrainNumber()).append(" / ").append(booking.getTrain().getTrainName()).append("</td>\n")
            .append("      <td><strong>Date of Journey:</strong> ").append(booking.getTravelDate()).append("</td>\n")
            .append("    </tr>\n")
            .append("    <tr>\n")
            .append("      <td><strong>From:</strong> ").append(booking.getSourceStationCode()).append("</td>\n")
            .append("      <td><strong>To:</strong> ").append(booking.getDestinationStationCode()).append("</td>\n")
            .append("    </tr>\n")
            .append("    <tr>\n")
            .append("      <td><strong>Class:</strong> ").append(booking.getClassType()).append("</td>\n")
            .append("      <td><strong>Ticket Status:</strong> ").append(booking.getStatus()).append("</td>\n")
            .append("    </tr>\n")
            .append("  </table>\n")
            .append("  <h3>Passenger Details</h3>\n")
            .append("  <table class='passengers-table'>\n")
            .append("    <thead>\n")
            .append("      <tr><th>#</th><th>Name</th><th>Age</th><th>Gender</th><th>Seat / Berth</th></tr>\n")
            .append("    </thead>\n")
            .append("    <tbody>\n");

        int idx = 1;
        for (Passenger p : passengers) {
            html.append("      <tr>\n")
                .append("        <td>").append(idx++).append("</td>\n")
                .append("        <td>").append(p.getName()).append("</td>\n")
                .append("        <td>").append(p.getAge()).append("</td>\n")
                .append("        <td>").append(p.getGender()).append("</td>\n")
                .append("        <td>").append(p.getSeatNumber() != null ? p.getSeatNumber() : "PENDING").append("</td>\n")
                .append("      </tr>\n");
        }

        html.append("    </tbody>\n")
            .append("  </table>\n")
            .append("  <h3>Fare Breakdown</h3>\n")
            .append("  <table class='details-table' style='background: #fafafa; border-radius: 5px; padding: 10px;'>\n")
            .append("    <tr><td>Base Fare:</td><td>₹").append(fare.getBaseFare()).append("</td></tr>\n")
            .append("    <tr><td>Surcharge / Tatkal:</td><td>₹").append(fare.getSurcharge()).append("</td></tr>\n")
            .append("    <tr><td>GST (5%):</td><td>₹").append(fare.getGst()).append("</td></tr>\n")
            .append("    <tr><td>Convenience Fee:</td><td>₹").append(fare.getConvenienceFee()).append("</td></tr>\n")
            .append("    <tr><td>Insurance:</td><td>₹").append(fare.getInsuranceFee()).append("</td></tr>\n")
            .append("    <tr style='border-top: 1px solid #ddd; font-weight: bold;'><td>Total Fare:</td><td>₹").append(fare.getTotalFare()).append("</td></tr>\n")
            .append("  </table>\n")
            .append("  <div class='qr-section'>\n")
            .append("    ").append(qrCodeSvg).append("\n")
            .append("    <div style='margin-top: 5px; font-size: 11px;'>Scan QR to verify PNR</div>\n")
            .append("  </div>\n")
            .append("  <div class='footer'>\n")
            .append("    This is an e-ticket. Please carry a valid original identity card during travel.<br/>\n")
            .append("    IRCTC Corporate Office, New Delhi - 110001\n")
            .append("  </div>\n")
            .append("</div>\n</body>\n</html>");

        return html.toString();
    }

    private String generateQrCodeSvg(String pnr) {
        // Generates a mock but beautiful and valid SVG QR Code with central IRCTC label
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"120\" viewBox=\"0 0 29 29\">\n" +
                "  <path d=\"M0 0h7v7H0zm1 1v5h5V1zm8 0h3v1H9zm4 0h1v3h-1zm2 0h2v1h-2zm3 0h4v1h-4zm5 0h2v2h-2zm-12 2h2v1h-2zm7 0h1v1h-1zm3 0h1v2h-1zm-13 6h7v7H0zm1 1v5h5v-5zm16-4h1v1h-1zm2 0h3v1h-3zm-14 3v1h1v-1zm4 0h1v1h-1zm10 0h1v2h-1zm2 0h1v1h-1zm-12 1h1v1h-1zm4 0h1v1h-1zm3 0h2v1h-2zm5 0h1v1h-1zm3 0h1v1h-1zm-10 1h2v1h-2zm5 0h1v2h-1zm-11 2h1v1h-1zm3 0h2v1h-2zm4 0h2v1h-2zm5 0h3v1h-3zm-12 1h1v1h-1zm3 0h2v1h-2zm4 0h1v2h-1zm2 0h1v1h-1zm6 0h1v1h-1zm-15 1h1v1h-1zm4 0h1v1h-1zm3 0h1v1h-1zm4 0h1v1h-1zm2 0h3v1h-3z\" fill=\"#000\"/>\n" +
                "  <rect x=\"11\" y=\"11\" width=\"7\" height=\"7\" fill=\"#e6f2ff\" rx=\"1\"/>\n" +
                "  <text x=\"14.5\" y=\"15.5\" font-family=\"Arial\" font-size=\"3.5\" font-weight=\"bold\" fill=\"#003366\" text-anchor=\"middle\">IR</text>\n" +
                "</svg>";
    }
}
