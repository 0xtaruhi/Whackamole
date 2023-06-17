`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2023/05/15 14:08:26
// Design Name: 
// Module Name: test_key_lcd
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module test_key(
  input               clk,
  input               rst,
  output reg[3:0]     c_pin,
  input[3:0]          r_pin,
  output reg[3:0]     key_out,
  output reg          o_key_out_en
);
  reg[15:0]         div_cnt;
  reg[2:0]          state;
  reg               cnt_full;
  localparam        CHECK_R1=3'b000;
  localparam        CHECK_R2=3'b001;
  localparam        CHECK_R3=3'b011;
  localparam        CHECK_R4=3'b010;
  
  
  //filter out the key jitter
  reg  [3 :0]       r_pin_0buf;
  reg               r_pin_0key;
  reg  [3 :0]       r_pin_1buf;
  reg               r_pin_1key;
  reg  [3 :0]       r_pin_2buf;
  reg               r_pin_2key;
  reg  [3 :0]       r_pin_3buf;
  reg               r_pin_3key;
  wire [3 :0]       r_pin_key;
  
  
  always@(posedge clk or negedge rst)begin
    if(!rst)begin
      div_cnt <= 16'd0;
      cnt_full <= 1'b0;
    end
    else
      if(div_cnt==16'd49999)begin
      //if(div_cnt==16'd499)begin
        div_cnt <= 16'd0;
        cnt_full <= 1'b1;
      end
      else begin
        div_cnt <= div_cnt + 1'b1;
        cnt_full <= 1'b0;
      end
  end

  
//(* dont_touch = "true" *) ila_key u_ila_key (
//  .clk   (clk),
//  .probe0(cnt_full		),  //1bit
//  .probe1(r_pin_0buf	),	//4bit
//  .probe2(r_pin_1buf	),  //4bit
//  .probe3(r_pin_2buf	),	//4bit
//  .probe4(r_pin_3buf	),	//4bit
//  .probe5(r_pin_key		),	//4bit
//  .probe6(state			),  //3bit
//  .probe7(o_key_out_en	),  //1bit
//  .probe8(key_out		)   //4bit
//); 
  

//r_pin_0buf
always@(posedge clk or negedge rst)
begin
    if(!rst)
        r_pin_0buf <= 4'b0000;
    else if ( cnt_full == 1'b1 )
        r_pin_0buf <= 4'b0000;
    else if (( div_cnt == 16'd20000 ) || ( div_cnt == 16'd22000 ) || ( div_cnt == 16'd24000 ) || ( div_cnt == 16'd26000 ))
        r_pin_0buf <= {r_pin_0buf[2:0],r_pin[0]};
    else
        ;
end
always@(posedge clk)
begin
    if ( r_pin_0buf == 4'b1111 )
        r_pin_0key <= 1'b1;
    else if ( r_pin_0buf == 4'b0000 )
        r_pin_0key <= 1'b0;
    else
        ;
end
//r_pin_1buf
always@(posedge clk or negedge rst)
begin
    if(!rst)
        r_pin_1buf <= 4'b0000;
    else if ( cnt_full == 1'b1 )
        r_pin_1buf <= 4'b0000;
    else if (( div_cnt == 16'd20000 ) || ( div_cnt == 16'd22000 ) || ( div_cnt == 16'd24000 ) || ( div_cnt == 16'd26000 ))
        r_pin_1buf <= {r_pin_1buf[2:0],r_pin[1]};
    else
        ;
end
always@(posedge clk)
begin
    if ( r_pin_1buf == 4'b1111 )
        r_pin_1key <= 1'b1;
    else if ( r_pin_1buf == 4'b0000 )
        r_pin_1key <= 1'b0;
    else
        ;
end
//r_pin_2buf
always@(posedge clk or negedge rst)
begin
    if(!rst)
        r_pin_2buf <= 4'b0000;
    else if ( cnt_full == 1'b1 )
        r_pin_2buf <= 4'b0000;
    else if (( div_cnt == 16'd20000 ) || ( div_cnt == 16'd22000 ) || ( div_cnt == 16'd24000 ) || ( div_cnt == 16'd26000 ))
        r_pin_2buf <= {r_pin_2buf[2:0],r_pin[2]};
    else
        ;
end
always@(posedge clk)
begin
    if ( r_pin_2buf == 4'b1111 )
        r_pin_2key <= 1'b1;
    else if ( r_pin_2buf == 4'b0000 )
        r_pin_2key <= 1'b0;
    else
        ;
end
//r_pin_3buf
always@(posedge clk or negedge rst)
begin
    if(!rst)
        r_pin_3buf <= 4'b0000;
    else if ( cnt_full == 1'b1 )
        r_pin_3buf <= 4'b0000;
    else if (( div_cnt == 16'd20000 ) || ( div_cnt == 16'd22000 ) || ( div_cnt == 16'd24000 ) || ( div_cnt == 16'd26000 ))
        r_pin_3buf <= {r_pin_3buf[2:0],r_pin[3]};
    else
        ;
end
always@(posedge clk)
begin
    if ( r_pin_3buf == 4'b1111 )
        r_pin_3key <= 1'b1;
    else if ( r_pin_3buf == 4'b0000 )
        r_pin_3key <= 1'b0;
    else
        ;
end

assign r_pin_key = {r_pin_3key,r_pin_2key,r_pin_1key,r_pin_0key};


//determine which key was pressed
  always@(posedge clk)begin
    if(!rst)
      state <= CHECK_R1;
    else
      case(state)
        CHECK_R1:
          if(cnt_full)
            state <= CHECK_R2;
          else
            state <= CHECK_R1;
        CHECK_R2:
          if(cnt_full)
            state <= CHECK_R3;
          else
            state <= CHECK_R2;
        CHECK_R3:
          if(cnt_full)
            state <= CHECK_R4;
          else
            state <= CHECK_R3;
        CHECK_R4:
          if(cnt_full)
            state <= CHECK_R1;
          else
            state <= CHECK_R4;
        default:
          state <= state;
      endcase
  end

  always@(posedge clk or negedge rst)begin
    if(!rst)
      c_pin <= 4'b0000;
    else
      case(state)
        CHECK_R1:begin
          c_pin <= 4'b1000;
          case(r_pin_key)
            4'b1000:key_out <= 4'd10;  //a
            4'b0100:key_out <= 4'd3;
            4'b0010:key_out <= 4'd2;
            4'b0001:key_out <= 4'd1;
            default:;
          endcase
        end
        CHECK_R2:begin
          c_pin <= 4'b0100;
          case(r_pin_key)
            4'b1000:key_out <= 4'd13;  //d
            4'b0100:key_out <= 4'd14;  //e <--> #
            4'b0010:key_out <= 4'd0;
            4'b0001:key_out <= 4'd15;  //f <--> *
            default:;
          endcase
        end
        CHECK_R3:begin
          c_pin <= 4'b0010;
          case(r_pin_key)
            4'b1000:key_out <= 4'd12;  //c
            4'b0100:key_out <= 4'd9;
            4'b0010:key_out <= 4'd8;
            4'b0001:key_out <= 4'd7;
            default:;
          endcase
        end
        CHECK_R4:begin
          c_pin <= 4'b0001;
          case(r_pin_key)
            4'b1000:key_out <= 4'd11;  //b
            4'b0100:key_out <= 4'd6;
            4'b0010:key_out <= 4'd5;
            4'b0001:key_out <= 4'd4;
            default:;
          endcase
        end 
        default:begin
          c_pin <= 4'b0000;
          key_out <= 4'd0;
        end
      endcase
  end

//o_key_out_en
always@(posedge clk)
begin
    if (( cnt_full == 1'b1 ) && (( state == CHECK_R1 ) || ( state == CHECK_R2 ) || ( state == CHECK_R3 ) || ( state == CHECK_R4 )) &&
                                (( r_pin == 4'b1000  ) || ( r_pin == 4'b0100  ) || ( r_pin == 4'b0010  ) || ( r_pin == 4'b0001 )))
        o_key_out_en <= 1'b1;
    else
        o_key_out_en <= 1'b0;
end



endmodule
